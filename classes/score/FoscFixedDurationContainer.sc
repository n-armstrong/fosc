/* ------------------------------------------------------------------------------------------------------------
• FoscFixedDurationContainer

m = Array.fill(5, { |i| FoscNote(60+i, FoscDuration(1, 8)) });
x = FoscFixedDurationContainer(FoscDuration(3, 4), m);
x.music;
x.targetDuration.inspect;
x.inspect;

x.isFull;
x.isMisfilled;
x.isOverfull;
x.isUnderfull;
x.prCheckDuration;
x.targetDuration_([5, 8]);
x.targetDuration.pair;
------------------------------------------------------------------------------------------------------------ */
FoscFixedDurationContainer : FoscContainer {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <targetDuration;
	*new { |targetDuration, music, isSimultaneous=false, name|
		^super.new(music, isSimultaneous, name).initFoscFixedDurationContainer(targetDuration);
	}
	initFoscFixedDurationContainer { |argDuration|
		targetDuration = FoscDuration(argDuration ?? { FoscDuration(1, 4) });
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• isFull

	Is true when preprolated duration equals target duration. Otherwise false.
	Returns true or false.
	-------------------------------------------------------------------------------------------------------- */
	isFull {
		^(targetDuration == this.prGetPreprolatedDuration);
	}
	/* --------------------------------------------------------------------------------------------------------
	• isMisfilled

	Is true when preprolated duration does not equal target duration. Otherwise false.
	Returns true or false.
	-------------------------------------------------------------------------------------------------------- */
	isMisfilled {
		^this.isFull.not;
	}
	/* --------------------------------------------------------------------------------------------------------
	• isOverfull

	Is true when preprolated duration is greater than target duration. Otherwise false.
	Returns true or false.
	-------------------------------------------------------------------------------------------------------- */
	isOverfull {
		^(targetDuration < this.prGetPreprolatedDuration);
	}
	/* --------------------------------------------------------------------------------------------------------
	• isUnderfull

	Is true when preprolated duration is less than target duration. Otherwise false.
	Returns true or false.
	-------------------------------------------------------------------------------------------------------- */
	isUnderfull {
		^(this.prGetPreprolatedDuration < targetDuration);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• targetDuration_

	Sets target duration of fixed-duration container.
	-------------------------------------------------------------------------------------------------------- */
	targetDuration_ { |duration|
		targetDuration = FoscDuration(duration);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• prCheckDuration
	-------------------------------------------------------------------------------------------------------- */
	prCheckDuration {
		var preProlatedDuration;
		preProlatedDuration = this.prGetContentsDuration;
		if (preProlatedDuration < targetDuration) { ^throw("% is underfull".format(this.species)) };
		if (targetDuration < preProlatedDuration) { ^throw("% is overfull".format(this.species)) };
	}
	/* --------------------------------------------------------------------------------------------------------
	• prGetLilypondFormat
	-------------------------------------------------------------------------------------------------------- */
	prGetLilypondFormat {
		this.prCheckDuration;
		^this.prFormatComponent;
	}
}
