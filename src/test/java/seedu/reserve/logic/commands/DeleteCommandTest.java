package seedu.reserve.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.reserve.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.reserve.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.reserve.logic.commands.CommandTestUtil.showCustomerAtIndex;
import static seedu.reserve.testutil.TypicalIndexes.INDEX_FIRST_CUSTOMER;
import static seedu.reserve.testutil.TypicalIndexes.INDEX_SECOND_CUSTOMER;
import static seedu.reserve.testutil.TypicalReservation.getTypicalReserveMate;

import org.junit.jupiter.api.Test;

import seedu.reserve.commons.core.index.Index;
import seedu.reserve.logic.Messages;
import seedu.reserve.model.Model;
import seedu.reserve.model.ModelManager;
import seedu.reserve.model.UserPrefs;
import seedu.reserve.model.reservation.Reservation;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalReserveMate(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Reservation reservationToDelete = model.getFilteredReservationList().get(INDEX_FIRST_CUSTOMER.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_CUSTOMER);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_RESERVATION_SUCCESS,
                INDEX_FIRST_CUSTOMER.getOneBased());

        ModelManager expectedModel = new ModelManager(model.getReserveMate(), new UserPrefs());
        expectedModel.deleteReservation(reservationToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredReservationList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_RESERVATION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showCustomerAtIndex(model, INDEX_FIRST_CUSTOMER);

        Reservation reservationToDelete = model.getFilteredReservationList().get(INDEX_FIRST_CUSTOMER.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_CUSTOMER);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_RESERVATION_SUCCESS,
               INDEX_FIRST_CUSTOMER.getOneBased());

        Model expectedModel = new ModelManager(model.getReserveMate(), new UserPrefs());
        expectedModel.deleteReservation(reservationToDelete);
        showNoCustomer(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showCustomerAtIndex(model, INDEX_FIRST_CUSTOMER);

        Index outOfBoundIndex = INDEX_SECOND_CUSTOMER;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getReserveMate().getReservationList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_RESERVATION_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_CUSTOMER);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_CUSTOMER);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_CUSTOMER);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different reservation -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoCustomer(Model model) {
        model.updateFilteredReservationList(p -> false);

        assertTrue(model.getFilteredReservationList().isEmpty());
    }
}
