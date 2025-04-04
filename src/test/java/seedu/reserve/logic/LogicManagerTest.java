package seedu.reserve.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.reserve.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.reserve.logic.Messages.MESSAGE_INVALID_RESERVATION_DISPLAYED_INDEX;
import static seedu.reserve.logic.commands.CommandTestUtil.DATETIME_DESC_AMY;
import static seedu.reserve.logic.commands.CommandTestUtil.DINERS_DESC_AMY;
import static seedu.reserve.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.reserve.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.reserve.logic.commands.CommandTestUtil.OCC_DESC_ANNIVERSARY;
import static seedu.reserve.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_OCCASION_ANNIVERSARY;
import static seedu.reserve.testutil.Assert.assertThrows;
import static seedu.reserve.testutil.TypicalReservation.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.reserve.logic.commands.AddCommand;
import seedu.reserve.logic.commands.CommandResult;
import seedu.reserve.logic.commands.HelpCommand;
import seedu.reserve.logic.commands.exceptions.CommandException;
import seedu.reserve.logic.parser.exceptions.ParseException;
import seedu.reserve.model.Model;
import seedu.reserve.model.ModelManager;
import seedu.reserve.model.ReadOnlyReserveMate;
import seedu.reserve.model.UserPrefs;
import seedu.reserve.model.reservation.Reservation;
import seedu.reserve.storage.JsonReserveMateStorage;
import seedu.reserve.storage.JsonUserPrefsStorage;
import seedu.reserve.storage.StorageManager;
import seedu.reserve.testutil.ReservationBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        JsonReserveMateStorage reserveMateStorage =
            new JsonReserveMateStorage(temporaryFolder.resolve("reservemate.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(reserveMateStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_RESERVATION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
            LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
            LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredReservationList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredReservationList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
                                      Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertModelsEqual(expectedModel, model);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String helpcommand = HelpCommand.COMMAND_WORD;
        assertCommandSuccess(helpcommand, HelpCommand.SHOWING_HELP_MESSAGE, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage) {
        Model expectedModel = new ModelManager(model.getReserveMate(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertModelsEqual(expectedModel, model);
    }

    /**
     * Asserts that two models are equal by comparing their contents
     */
    private void assertModelsEqual(Model expected, Model actual) {
        assertEquals(expected.getReserveMate(), actual.getReserveMate(),
            "ReserveMate contents should be equal");
        assertEquals(expected.getFilteredReservationList(), actual.getFilteredReservationList(),
            "Filtered reservation lists should be equal");
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an ReserveMateStorage that throws the IOException e when saving
        JsonReserveMateStorage reserveMateStorage = new JsonReserveMateStorage(prefPath) {
            @Override
            public void saveReserveMate(ReadOnlyReserveMate reserveMate, Path filePath)
                    throws IOException {
                    throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
            new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(reserveMateStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveReserveMate method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
            + EMAIL_DESC_AMY + DINERS_DESC_AMY + DATETIME_DESC_AMY + OCC_DESC_ANNIVERSARY;
        Reservation expectedReservation = new ReservationBuilder(AMY).withOccasions(VALID_OCCASION_ANNIVERSARY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addReservation(expectedReservation);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
