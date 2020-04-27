/* ---------------------------------------------------------------------------------------------------------------
• FoscOctave

inspect(FoscOctave(7));
inspect(FoscOctave("A-1"));
inspect(FoscOctave("cs''"));
inspect(FoscOctave("A")); // octaveNumber defaults to 4
inspect(FoscOctave("c")); // octaveNumber defaults to 4
--------------------------------------------------------------------------------------------------------------- */
FoscOctave : FoscObject {
	var <octaveNumber, <octaveName;
	classvar manager;
	*new { |val|
		var octaveNumber;

		manager = FoscPitchNameManager;

		octaveNumber = case
		{ val.isNumber } { val.asInteger }
		{ val.isKindOf(FoscOctave) } { val.octaveNumber }
		{ val.isKindOf(FoscPitch) } { val.octaveNumber }
		{ val.isKindOf(String) } {
			case
			{ val.isPitchName } { val.octaveName }
			{ val.isLilyPondPitchName } { manager.lilyPondOctaveNumberToOctaveNumber(val.lilyPondOctaveName) }
			{ val.isPitchClassName } { 4 }
			{ val.isLilyPondPitchClassName } { 4 }
		}
		{ Error("Can not initialize % from: %".format(this.name, val) ) }

		^super.new.init(octaveNumber.asInteger);
	}
	init { |argOctaveNumber|
		octaveNumber = argOctaveNumber;
		octaveName = octaveNumber.asString;
	}
	inspect {
		super.inspect(#[octaveNumber, octaveName, lpStr]);
	}
	lpStr {
		^manager.octaveNumberToLilyPondOctaveName(octaveNumber);
	}
}