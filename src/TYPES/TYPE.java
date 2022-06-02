package TYPES;

import SYMBOL_TABLE.KindEnum;

import java.util.Objects;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;
	public TypeEnum typeEnum;

	/*************/
	/* isClass() */
	/*************/
	public boolean isClass() { return false; }

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray() { return false; }

	@Override
	public boolean equals(Object given)
	{
		if (!(given instanceof TYPE)) return false;
		return (this.typeEnum == ((TYPE) given).typeEnum);
	}
}
