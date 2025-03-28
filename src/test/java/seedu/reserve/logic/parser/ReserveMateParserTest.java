package seedu.reserve.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.reserve.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.reserve.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.reserve.testutil.Assert.assertThrows;
import static seedu.reserve.testutil.TypicalIndexes.INDEX_FIRST_RESERVATION;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.reserve.logic.commands.AddCommand;
import seedu.reserve.logic.commands.ClearCommand;
import seedu.reserve.logic.commands.DeleteCommand;
import seedu.reserve.logic.commands.EditCommand;
import seedu.reserve.logic.commands.ExitCommand;
import seedu.reserve.logic.commands.FindCommand;
import seedu.reserve.logic.commands.HelpCommand;
import seedu.reserve.logic.commands.ListCommand;
import seedu.reserve.logic.parser.exceptions.ParseException;
import seedu.reserve.model.reservation.NameContainsKeywordsPredicate;
import seedu.reserve.model.reservation.Reservation;
import seedu.reserve.testutil.EditReservationDescriptorBuilder;
import seedu.reserve.testutil.ReservationBuilder;
import seedu.reserve.testutil.ReservationUtil;


public class ReserveMateParserTest {

    private final ReserveMateParser parser = new ReserveMateParser();

    @Test
    public void parseCommand_add() throws Exception {
        Reservation reservation = new ReservationBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(ReservationUtil.getAddCommand(reservation));
        assertEquals(new AddCommand(reservation), command);
    }

    @Test
    public void parseCommand_addMixedCase() throws Exception {
        Reservation reservation = new ReservationBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand("aDd "
                + ReservationUtil.getAddCommand(reservation).substring(4));
        assertEquals(new AddCommand(reservation), command);
    }


    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_clearMixedCase() throws Exception {
        assertTrue(parser.parseCommand("ClEaR") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_RESERVATION.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_RESERVATION, true), command);
    }

    @Test
    public void parseCommand_deleteMixedCase() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand("DeLeTe "
                + INDEX_FIRST_RESERVATION.getOneBased() + " confirm");
        assertEquals(new DeleteCommand(INDEX_FIRST_RESERVATION, true), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Reservation reservation = new ReservationBuilder().build();
        EditCommand.EditReservationDescriptor descriptor = new EditReservationDescriptorBuilder(reservation).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_RESERVATION.getOneBased() + " "
                + ReservationUtil.getEditReservationDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_RESERVATION, descriptor), command);
    }

    @Test
    public void parseCommand_editMixedCase() throws Exception {
        Reservation reservation = new ReservationBuilder().build();
        EditCommand.EditReservationDescriptor descriptor = new EditReservationDescriptorBuilder(reservation).build();
        EditCommand command = (EditCommand) parser.parseCommand("EdIt " + INDEX_FIRST_RESERVATION.getOneBased() + " "
                + ReservationUtil.getEditReservationDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_RESERVATION, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_exitMixedCase() throws Exception {
        assertTrue(parser.parseCommand("ExIt") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_findMixedCase() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand("FiNd " + String.join(" ", keywords));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_helpMixedCase() throws Exception {
        assertTrue(parser.parseCommand("HeLp") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_listMixedCase() throws Exception {
        assertTrue(parser.parseCommand("LiSt") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            HelpCommand.MESSAGE_USAGE), () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

}
