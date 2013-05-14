/**
 * @author Alykov Gali
 * @date 13.05.2013
 */
package ru.cinimex.client.commands;

import java.util.HashMap;
import ru.cinimex.data.Header;

public class ReactionClientCommandFactory {
	HashMap<Header, ClientReactionCommand> commands;
	public ReactionClientCommandFactory() {
		commands = new HashMap<Header, ClientReactionCommand>();
		commands.put(Header.BAD_INIT, new ReactionOnBadInit());
		commands.put(Header.TKO_WIN, new ReactionOnTKOWin());
		commands.put(Header.WIN, new ReactionOnWin());
		commands.put(Header.TKO_LOSE, new ReactionOnTKOLose());
		commands.put(Header.LOSE, new ReactionOnLose());
		commands.put(Header.BIG_BANG, new ReactionOnBigBang());
		commands.put(Header.STRIKE, new ReactionOnStrike());
		commands.put(Header.STROKE, new ReactionOnStroke());
		commands.put(Header.NOT_STROKE, new ReactionOnNotStroke());
		commands.put(Header.INIT, new ReactionOnInit());
	}
	
	public ClientReactionCommand getCommand(Header header) {
		return commands.get(header);
	}
}
