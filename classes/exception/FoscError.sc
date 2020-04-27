/* ------------------------------------------------------------------------------------------------------------
• FoscAssertionError

FoscAssertionError("this is an error message");

"SDFDF".assert;

Error("SDFDF").throw
------------------------------------------------------------------------------------------------------------ */
FoscAssertionError {
	*new { |message=""|
		//message = "%:%: %".format(this.class.name, thisMethod.name, message);
		^super.new(message);
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscAssignabilityError
FoscAssignabilityError();
------------------------------------------------------------------------------------------------------------ */
FoscAssignabilityError : Error {
	*new {
		^super.new("Duration can not be assigned to note, rest or chord.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscExtraSpannerError
FoscExtraSpannerError();
------------------------------------------------------------------------------------------------------------ */
FoscExtraSpannerError : Error {
	*new {
		^super.new("More than one spanner found for single-spanner operation.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscImpreciseTempoError
FoscImpreciseTempoError();
------------------------------------------------------------------------------------------------------------ */
FoscImpreciseTempoError : Error {
	*new {
		^super.new("Tempo is imprecise.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscMissingMeasureError
FoscMissingMeasureError();
------------------------------------------------------------------------------------------------------------ */
FoscMissingMeasureError : Error {
	*new {
		^super.new("No measure found.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscMissingSpannerError
FoscMissingSpannerError();
------------------------------------------------------------------------------------------------------------ */
FoscMissingSpannerError : Error {
	*new {
		^super.new("No spanner found.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscMissingTempoError
FoscMissingTempoError();
------------------------------------------------------------------------------------------------------------ */
FoscMissingTempoError : Error {
	*new {
		^super.new("No tempo found.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscOverfullContainerError
FoscOverfullContainerError();
------------------------------------------------------------------------------------------------------------ */
FoscOverfullContainerError : Error {
	*new {
		^super.new("Container contents duration is greater than container target duration.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscParentageError
FoscParentageError();
------------------------------------------------------------------------------------------------------------ */
FoscParentageError : Error {
	*new {
		^super.new("A parentage error.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeParserFinishedError
FoscSchemeParserFinishedError();
------------------------------------------------------------------------------------------------------------ */
FoscSchemeParserFinishedError : Error {
	*new {
		^super.new("SchemeParser has finished parsing.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscTypeError
FoscTypeError();
------------------------------------------------------------------------------------------------------------ */
FoscTypeError : Error {
	*new {
		^super.new("Type error.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscUnboundedTimeIntervalError
FoscUnboundedTimeIntervalError();
------------------------------------------------------------------------------------------------------------ */
FoscUnboundedTimeIntervalError : Error {
	*new {
		^super.new("Time interval has no bounds.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscUnderfullContainerError
FoscUnderfullContainerError();
------------------------------------------------------------------------------------------------------------ */
FoscUnderfullContainerError : Error {
	*new {
		^super.new("Container contents duration is less than container target duration.").throw;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscWellformednessError
FoscWellformednessError();
------------------------------------------------------------------------------------------------------------ */
FoscWellformednessError : Error {
	*new {
		^super.new("Score not well formed.").throw;
	}
}