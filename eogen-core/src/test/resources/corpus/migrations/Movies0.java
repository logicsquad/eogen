package webobjectsexamples.businesslogic.movies.common;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

import java.sql.Types;

/**
 * Creates the tables of the Movies model.
 */
public class Movies0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("Movies0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable directorTable = database.newTableNamed("director");
		directorTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		directorTable.newIntegerColumn("TALENT_ID", NOT_NULL);
		directorTable.create();
		directorTable.setPrimaryKey("MOVIE_ID", "TALENT_ID");

		ERXMigrationTable movieTable = database.newTableNamed("movie");
		movieTable.newStringColumn("CATEGORY", 20, ALLOWS_NULL);
		movieTable.newTimestampColumn("DATE_RELEASED", ALLOWS_NULL);
		movieTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		movieTable.newIntegerColumn("poster_AttachmentID", ALLOWS_NULL);
		movieTable.newStringColumn("POSTER_NAME", 255, ALLOWS_NULL);
		movieTable.newStringColumn("RATED", 10, ALLOWS_NULL);
		movieTable.newBigDecimalColumn("REVENUE", 10, 2, ALLOWS_NULL);
		movieTable.newIntegerColumn("STUDIO_ID", ALLOWS_NULL);
		movieTable.newStringColumn("TITLE", 255, NOT_NULL);
		movieTable.newStringColumn("TRAILER_NAME", 255, ALLOWS_NULL);
		movieTable.create();
		movieTable.setPrimaryKey("MOVIE_ID");

		ERXMigrationTable movieRoleTable = database.newTableNamed("movierole");
		movieRoleTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		movieRoleTable.newStringColumn("ROLE_NAME", 30, ALLOWS_NULL);
		movieRoleTable.newIntegerColumn("TALENT_ID", NOT_NULL);
		movieRoleTable.create();
		movieRoleTable.setPrimaryKey("MOVIE_ID", "TALENT_ID");

		ERXMigrationTable plotSummaryTable = database.newTableNamed("PlotSummary");
		plotSummaryTable.newColumn("MOVIE_ID", Types.INTEGER, 0, 9, 0, NOT_NULL, null);
		plotSummaryTable.newLargeStringColumn("SUMMARY", ALLOWS_NULL);
		plotSummaryTable.create();
		plotSummaryTable.setPrimaryKey("MOVIE_ID");

		ERXMigrationTable reviewTable = database.newTableNamed("review");
		reviewTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		reviewTable.newLargeStringColumn("REVIEW", ALLOWS_NULL);
		reviewTable.newStringColumn("REVIEWER", 50, ALLOWS_NULL);
		reviewTable.newIntegerColumn("REVIEW_ID", NOT_NULL);
		reviewTable.create();
		reviewTable.setPrimaryKey("REVIEW_ID");

		ERXMigrationTable studioTable = database.newTableNamed("studio");
		studioTable.newBigDecimalColumn("BUDGET", 15, 2, NOT_NULL);
		studioTable.newStringColumn("NAME", 40, NOT_NULL);
		studioTable.newIntegerColumn("STUDIO_ID", NOT_NULL);
		studioTable.create();
		studioTable.setPrimaryKey("STUDIO_ID");

		ERXMigrationTable talentTable = database.newTableNamed("talent");
		talentTable.newStringColumn("FIRST_NAME", 20, NOT_NULL);
		talentTable.newStringColumn("LAST_NAME", 30, NOT_NULL);
		talentTable.newColumn("TALENT_ID", Types.INTEGER, 0, 9, 0, NOT_NULL, null);
		talentTable.create();
		talentTable.setPrimaryKey("TALENT_ID");

		ERXMigrationTable talentPhotoTable = database.newTableNamed("talentphoto");
		talentPhotoTable.newBlobColumn("PHOTO", ALLOWS_NULL);
		talentPhotoTable.newIntegerColumn("TALENT_ID", NOT_NULL);
		talentPhotoTable.create();
		talentPhotoTable.setPrimaryKey("TALENT_ID");

		ERXMigrationTable votingTable = database.newTableNamed("voting");
		votingTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		votingTable.newIntegerColumn("NUMBER_OF_VOTES", ALLOWS_NULL);
		votingTable.newDoubleColumn("RUNNING_AVERAGE", 0, 0, ALLOWS_NULL);
		votingTable.create();
		votingTable.setPrimaryKey("MOVIE_ID");

		directorTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");
		directorTable.addForeignKey("TALENT_ID", "talent", "TALENT_ID");

		movieTable.addForeignKey("poster_AttachmentID", "ERAttachment", "id");
		movieTable.addForeignKey("STUDIO_ID", "studio", "STUDIO_ID");

		movieRoleTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");
		movieRoleTable.addForeignKey("TALENT_ID", "talent", "TALENT_ID");

		plotSummaryTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");

		reviewTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");

		talentPhotoTable.addForeignKey("TALENT_ID", "talent", "TALENT_ID");

		votingTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");
	}
}
