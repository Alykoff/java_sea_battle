/**
 * @author Alykov Gali
 * @date 09.04.2013
 */
package ru.cinimex.connector;

import java.io.Serializable;

public enum Header implements Serializable {
	READY,
	STROKE,
	BIG_BANG,
	STRIKE,
	MISS,
	NOT_STROKE,
	TKO_WIN,
	TKO_LOOSE,
	LOOSE,
	WIN,
	INIT,
	BAD_STROKE,
	BAD_INIT
}
