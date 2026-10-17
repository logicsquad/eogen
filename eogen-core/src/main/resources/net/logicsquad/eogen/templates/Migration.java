#if ($migration.packageName)
package ${migration.packageName};

#end
#foreach ($line in $migration.importLines(["er.extensions.migration.ERXMigrationFailedException"]))
$line
#end

/**
#if ($migration.first)
 * Creates the tables of the ${migration.modelName} model.
#else
 * Changes the tables of the ${migration.modelName} model as it changed since ${migration.since}:
 * <ul>
#foreach ($change in $migration.changes)
 * <li>${change}</li>
#end
 * </ul>
#end
 */
public class ${migration.className} extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("${migration.className} can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
#foreach ($line in $migration.upgradeLines)
$line
#end
	}
}
