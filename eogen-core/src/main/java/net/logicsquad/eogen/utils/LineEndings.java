package net.logicsquad.eogen.utils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Compares and writes generated files as a Windows checkout with Git's
 * {@code core.autocrlf} has them: a file that differs only in its line
 * endings is the same, and one that changes keeps the line endings it had.
 */
public final class LineEndings {
	private LineEndings() {
	}

	/**
	 * Returns whether two files' contents are the same, apart from their line
	 * endings.
	 *
	 * @param contents generated contents
	 * @param existing a file's contents
	 * @return {@code true} if they are
	 */
	public static boolean same(byte[] contents, byte[] existing) {
		return Arrays.equals(contents, existing) || Arrays.equals(toLF(contents), toLF(existing));
	}

	/**
	 * Returns generated contents with the line endings of the file they
	 * replace.
	 *
	 * @param contents generated contents, with LF line endings
	 * @param existing the file's contents
	 * @return contents with CRLF line endings if the file has them, or else
	 *         as they are
	 */
	public static byte[] like(byte[] contents, byte[] existing) {
		return usesCRLF(existing) ? toCRLF(contents) : contents;
	}

	/**
	 * Returns contents with each CRLF line ending as an LF.
	 *
	 * @param contents contents
	 * @return contents with LF line endings
	 */
	public static byte[] toLF(byte[] contents) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(contents.length);
		for (int i = 0; i < contents.length; i++) {
			if (!(contents[i] == '\r' && i + 1 < contents.length && contents[i + 1] == '\n')) {
				out.write(contents[i]);
			}
		}
		return out.toByteArray();
	}

	/**
	 * Returns contents with each LF line ending as a CRLF.
	 *
	 * @param contents contents
	 * @return contents with CRLF line endings
	 */
	public static byte[] toCRLF(byte[] contents) {
		byte[] lf = toLF(contents);
		ByteArrayOutputStream out = new ByteArrayOutputStream(lf.length + lf.length / 20);
		for (byte b : lf) {
			if (b == '\n') {
				out.write('\r');
			}
			out.write(b);
		}
		return out.toByteArray();
	}

	/**
	 * Returns whether contents end their first line with a CRLF.
	 *
	 * @param contents contents
	 * @return {@code true} if they do
	 */
	public static boolean usesCRLF(byte[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == '\n') {
				return i > 0 && contents[i - 1] == '\r';
			}
		}
		return false;
	}
}
