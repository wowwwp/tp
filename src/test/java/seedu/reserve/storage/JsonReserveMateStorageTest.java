package seedu.reserve.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.reserve.testutil.Assert.assertThrows;
import static seedu.reserve.testutil.TypicalReservation.ALICE;
import static seedu.reserve.testutil.TypicalReservation.HOON;
import static seedu.reserve.testutil.TypicalReservation.IDA;
import static seedu.reserve.testutil.TypicalReservation.getTypicalReserveMate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.reserve.commons.exceptions.DataLoadingException;
import seedu.reserve.model.ReadOnlyReserveMate;
import seedu.reserve.model.ReserveMate;

public class JsonReserveMateStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonReserveMateStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readReserveMate_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readReserveMate(null));
    }

    private java.util.Optional<ReadOnlyReserveMate> readReserveMate(String filePath) throws Exception {
        return new JsonReserveMateStorage(Paths.get(filePath)).readReserveMate(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readReserveMate("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readReserveMate("notJsonFormatReserveMate.json"));
    }

    @Test
    public void readReserveMate_invalidReservationReserveMate_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readReserveMate("invalidReservationReserveMate.json"));
    }

    @Test
    public void readReserveMate_invalidAndValidReservationReserveMate_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readReserveMate("invalidAndValidReservationReserveMate.json"));
    }

    @Test
    public void readAndSaveReserveMate_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempReserveMate.json");
        ReserveMate original = getTypicalReserveMate();
        JsonReserveMateStorage jsonReserveMateStorage = new JsonReserveMateStorage(filePath);

        // Save in new file and read back
        jsonReserveMateStorage.saveReserveMate(original, filePath);
        ReadOnlyReserveMate readBack = jsonReserveMateStorage.readReserveMate(filePath).get();
        assertEquals(original, new ReserveMate(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addReservation(HOON);
        original.removeReservation(ALICE);
        jsonReserveMateStorage.saveReserveMate(original, filePath);
        readBack = jsonReserveMateStorage.readReserveMate(filePath).get();
        assertEquals(original, new ReserveMate(readBack));

        // Save and read without specifying file path
        original.addReservation(IDA);
        jsonReserveMateStorage.saveReserveMate(original); // file path not specified
        readBack = jsonReserveMateStorage.readReserveMate().get(); // file path not specified
        assertEquals(original, new ReserveMate(readBack));

    }

    @Test
    public void saveReserveMate_nullReserveMate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveReserveMate(null, "SomeFile.json"));
    }

    /**
     * Saves {@code ReserveMate} at the specified {@code filePath}.
     */
    private void saveReserveMate(ReadOnlyReserveMate reserveMate, String filePath) {
        try {
            new JsonReserveMateStorage(Paths.get(filePath))
                    .saveReserveMate(reserveMate, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveReserveMate_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveReserveMate(new ReserveMate(), null));
    }
}
