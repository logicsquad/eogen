package er.extensions.migration;

public class ERXMigrationFailedException extends RuntimeException {
	public ERXMigrationFailedException(String message) {
		super(message);
	}

	public ERXMigrationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
