package seedu.reserve.model.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_DATETIME_BOB;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_DINERS_BOB;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_OCCASION_BIRTHDAY;
import static seedu.reserve.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.reserve.testutil.Assert.assertThrows;
import static seedu.reserve.testutil.TypicalReservation.ALICE;
import static seedu.reserve.testutil.TypicalReservation.BOB;

import org.junit.jupiter.api.Test;

import seedu.reserve.testutil.ReservationBuilder;

public class ReservationTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Reservation reservation = new ReservationBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> reservation.getTags().remove(0));
    }

    @Test
    public void isSameReservation() {
        // same object -> returns true
        assertTrue(ALICE.isSameReservation(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameReservation(null));

        // same name, phone number and date time, all other attributes different -> returns true
        Reservation editedAlice = new ReservationBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .withTags(VALID_OCCASION_BIRTHDAY).build();
        assertTrue(ALICE.isSameReservation(editedAlice));

        // different diners, all other attributes same -> returns false
        editedAlice = new ReservationBuilder(ALICE).withDiners(VALID_DINERS_BOB).build();
        assertTrue(ALICE.isSameReservation(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new ReservationBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameReservation(editedAlice));

        // different phone number, all other attributes same -> returns false
        editedAlice = new ReservationBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.isSameReservation(editedAlice));

        // different date and time, all other attributes same -> returns false
        editedAlice = new ReservationBuilder(ALICE).withDateTime(VALID_DATETIME_BOB).build();
        assertFalse(ALICE.isSameReservation(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Reservation editedBob = new ReservationBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameReservation(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new ReservationBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameReservation(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Reservation aliceCopy = new ReservationBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // same object hashcode -> return true
        assertTrue(ALICE.hashCode() == ALICE.hashCode());

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different reservation -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Reservation editedAlice = new ReservationBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new ReservationBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new ReservationBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ReservationBuilder(ALICE).withTags(VALID_OCCASION_BIRTHDAY).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Reservation.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone="
                + ALICE.getPhone() + ", email=" + ALICE.getEmail() + ", diners="
                + ALICE.getDiners() + ", dateTime=" + ALICE.getDateTime() + ", occasion=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
