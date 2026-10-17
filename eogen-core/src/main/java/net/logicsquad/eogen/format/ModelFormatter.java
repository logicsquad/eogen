package net.logicsquad.eogen.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;

import net.logicsquad.eogen.format.FormatResult.FormattedFile;
import net.logicsquad.eogen.format.FormatResult.Status;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOStoredProcedure;

/**
 * Rewrites a model as Entity Modeler saves it, so that one edited by hand, or
 * by a coding agent, looks as if Entity Modeler wrote it.
 * <p>
 * It saves the model with Entity Modeler's own writer into a scratch folder,
 * then compares each file with the model's, once parsed. Formatting must lose
 * nothing that a file says: no value may be dropped or changed, and no entry
 * removed from a list. It may drop an empty value, such as
 * {@code fetchSpecificationDictionary = {}}, and it may add what Entity
 * Modeler writes for the model as it loaded it, such as an inherited entity's
 * {@code parent} in {@code index.eomodeld}. Entity Modeler writes dictionaries
 * with their keys sorted, and most lists as sorted sets, so the order of a
 * list doesn't count, and neither does a duplicate in one. If formatting would
 * lose anything from any file, such as changing {@code YES} to {@code Y}, the
 * whole model is refused and left alone. Files that the model never loads,
 * such as an entity file that {@code index.eomodeld} doesn't list, are left
 * alone too.
 * </p>
 */
public final class ModelFormatter {
	private ModelFormatter() {
	}

	/**
	 * Formats a model.
	 *
	 * @param model the loaded and resolved model
	 * @param modelFolder its folder in the project
	 * @param scratch an empty folder to save it into
	 * @param write whether to write the formatted files to the model folder
	 * @return each of the model's files, and what formatting does to it
	 * @throws IOException if the model can't be saved or compared
	 */
	public static List<FormattedFile> format(EOModel model, File modelFolder, File scratch, boolean write) throws IOException {
		for (EOEntity entity : model.getEntities()) {
			entity.setEntityDirty(true);
			// An entity writes its .fspec only when its fetch specifications are.
			entity.setFetchSpecsDirty(true);
		}
		for (EOStoredProcedure storedProcedure : model.getStoredProcedures()) {
			storedProcedure.setStoredProcedureDirty(true);
		}
		File saved;
		try {
			saved = model.saveToFolder(scratch);
		} catch (Exception e) {
			throw new IOException("Failed to save " + model.getName() + ": " + e.getMessage(), e);
		}
		List<FormattedFile> files = new ArrayList<>();
		boolean refused = false;
		TreeSet<String> names = new TreeSet<>(managedFiles(saved));
		for (String name : names) {
			File original = new File(modelFolder, name);
			File formatted = new File(saved, name);
			if (original.isFile() && Arrays.equals(Files.readAllBytes(original.toPath()), Files.readAllBytes(formatted.toPath()))) {
				files.add(new FormattedFile(original, Status.UNCHANGED, null));
				continue;
			}
			String loss = loss(original, formatted);
			if (loss == null) {
				files.add(new FormattedFile(original, Status.FORMATTED, null));
			} else {
				files.add(new FormattedFile(original, Status.REFUSED, loss));
				refused = true;
			}
		}
		if (write && !refused) {
			for (FormattedFile file : files) {
				if (file.getStatus() == Status.FORMATTED) {
					Files.copy(new File(saved, file.getFile().getName()).toPath(), file.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		return files;
	}

	/**
	 * Describes the first thing that saving a model file with Entity Modeler's
	 * writer would lose from it, as {@link #format} names it in a refusal, or
	 * returns null if it would lose nothing. Saving may change the file's
	 * layout, drop an empty value, and add what Entity Modeler writes, as
	 * formatting may.
	 *
	 * @param file the model's file
	 * @param saved the same file, as Entity Modeler's writer saved the model
	 * @return what saving would lose, such as
	 *         {@code relationships[author].isMandatory would change from YES to Y},
	 *         or null
	 */
	public static String loss(File file, File saved) {
		if (!file.isFile()) {
			return "Entity Modeler would write " + saved.getName() + ", which the model doesn't have";
		}
		try {
			return difference(read(file), read(saved), "");
		} catch (Exception e) {
			return "it can't be read: " + e.getMessage();
		}
	}

	/**
	 * Describes the first thing that formatting would lose from a property list,
	 * as a path into it, such as {@code attributes[price].width}, or
	 * {@code qualifier.qualifiers[0].selectorName} in a list whose entries have
	 * no names, or returns null if it would lose nothing.
	 *
	 * @param original the model's
	 * @param formatted Entity Modeler's
	 * @param path where they are
	 * @return what would be lost, or null
	 */
	static String difference(Object original, Object formatted, String path) {
		if (original instanceof Map && formatted instanceof Map) {
			Map<?, ?> originalMap = (Map<?, ?>) original;
			Map<?, ?> formattedMap = (Map<?, ?>) formatted;
			for (Object key : new TreeSet<>(stringKeys(originalMap))) {
				Object value = originalMap.get(key);
				if (!formattedMap.containsKey(key)) {
					if (isEmpty(value)) {
						continue;
					}
					return join(path, String.valueOf(key)) + " would be dropped";
				}
				String difference = difference(value, formattedMap.get(key), join(path, String.valueOf(key)));
				if (difference != null) {
					return difference;
				}
			}
			return null;
		}
		if (original instanceof List && formatted instanceof List) {
			List<?> originalList = (List<?>) original;
			List<?> formattedList = (List<?>) formatted;
			if (allNamed(originalList) && (formattedList.isEmpty() || allNamed(formattedList))) {
				Map<String, Object> formattedByName = new TreeMap<>();
				for (Object item : formattedList) {
					formattedByName.put(name(item), item);
				}
				for (Object item : originalList) {
					String name = name(item);
					String itemPath = path + "[" + name + "]";
					if (!formattedByName.containsKey(name)) {
						return itemPath + " would be dropped";
					}
					String difference = difference(item, formattedByName.get(name), itemPath);
					if (difference != null) {
						return difference;
					}
				}
				return null;
			}
			TreeSet<String> originalItems = new TreeSet<>();
			for (Object item : originalList) {
				originalItems.add(canonical(item));
			}
			TreeSet<String> formattedItems = new TreeSet<>();
			for (Object item : formattedList) {
				formattedItems.add(canonical(item));
			}
			for (int i = 0; i < originalList.size(); i++) {
				Object item = originalList.get(i);
				if (!formattedItems.contains(canonical(item))) {
					// Entity Modeler writes the lists it doesn't sort, such as a
					// qualifier's, in their order, so a changed entry is in its place.
					Object counterpart = i < formattedList.size() ? formattedList.get(i) : null;
					if (item instanceof Map && counterpart instanceof Map && !originalItems.contains(canonical(counterpart))) {
						String difference = difference(item, counterpart, path + "[" + i + "]");
						if (difference != null) {
							return difference;
						}
					}
					return (path.isEmpty() ? "the list" : path) + " would lose " + (item instanceof String ? item : "an entry");
				}
			}
			return null;
		}
		if (original == null ? formatted == null : original.equals(formatted)) {
			return null;
		}
		if (original instanceof byte[] && formatted instanceof byte[] && Arrays.equals((byte[]) original, (byte[]) formatted)) {
			return null;
		}
		return (path.isEmpty() ? "the file" : path) + " would change from " + original + " to " + formatted;
	}

	private static boolean isEmpty(Object value) {
		return value instanceof Map && ((Map<?, ?>) value).isEmpty() || value instanceof List && ((List<?>) value).isEmpty()
				|| value instanceof String && ((String) value).isEmpty();
	}

	private static boolean allNamed(List<?> list) {
		for (Object item : list) {
			if (name(item) == null) {
				return false;
			}
		}
		return !list.isEmpty();
	}

	private static String name(Object item) {
		return item instanceof Map && ((Map<?, ?>) item).get("name") instanceof String ? (String) ((Map<?, ?>) item).get("name") : null;
	}

	private static Collection<String> stringKeys(Map<?, ?> map) {
		List<String> keys = new ArrayList<>();
		for (Object key : map.keySet()) {
			keys.add(String.valueOf(key));
		}
		return keys;
	}

	/**
	 * Returns a property list as a string that is the same for equal ones,
	 * whatever their order: maps by sorted key, lists sorted.
	 */
	static String canonical(Object object) {
		if (object instanceof Map) {
			Map<String, String> sorted = new TreeMap<>();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				sorted.put(String.valueOf(entry.getKey()), canonical(entry.getValue()));
			}
			return sorted.toString();
		}
		if (object instanceof List) {
			List<String> items = new ArrayList<>();
			for (Object item : (List<?>) object) {
				items.add(canonical(item));
			}
			Collections.sort(items);
			return items.toString();
		}
		if (object instanceof byte[]) {
			return Arrays.toString((byte[]) object);
		}
		return String.valueOf(object);
	}

	private static String join(String path, String key) {
		return path.isEmpty() ? key : path + "." + key;
	}

	private static Object read(File file) throws Exception {
		return WOLPropertyListSerialization.propertyListFromFile(file);
	}

	private static List<String> managedFiles(File folder) {
		List<String> names = new ArrayList<>();
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (file.isFile() && (name.equals("index.eomodeld") || name.endsWith(".plist") || name.endsWith(".fspec") || name.endsWith(".storedProcedure"))) {
					names.add(name);
				}
			}
		}
		return names;
	}
}
