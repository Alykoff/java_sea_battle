/**
 * @author Alykov Gali
 * @date 08.05.2013
 */
package ru.cinimex.server.commands;

import static ru.cinimex.server.FieldLogic.isBigBang;
import static ru.cinimex.server.FieldLogic.isStrike;
import static ru.cinimex.server.FieldLogic.isWin;
import ru.cinimex.data.Field;
import ru.cinimex.data.Point;

public class ReactionOnStrokeFactory {
	public ReactionOnStroke getStrokeCommand(Field notActiveField, Point point) {
		if (notActiveField == null) {
			throw new NullPointerException("notActiveClient nullpointer.");
		}
		if (isWin(notActiveField, point)) {
			return new ReactionOnWinStroke();
		} else if (isBigBang(notActiveField, point)) {
			return new ReactionOnBigBang();
		} else if (isStrike(notActiveField, point)) {
			return new ReactionOnStrike();
		} else {
			return new ReactionOnMiss();
		}
	}
}
