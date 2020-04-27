/* ------------------------------------------------------------------------------------------------------------
• FoscPitchNameManager

!!!TODO:
- see abjad/pitch/constants.py
- rename FoscPitchConstants
------------------------------------------------------------------------------------------------------------ */
FoscPitchNameManager : FoscObject {
	classvar accidentalNameToLilypondAccidentalName, accidentalNameToSemitones;
	classvar diatonicPCNumberToDiatonicPitchClassNumber, diatonicPitchClassNameToDiatonicPitchClassNumber;
	classvar lilypondAccidentalNameToAccidentalName;
	classvar octaveNumberToLilypondOctaveName, pitchClassNameToDiatonicPitchClassNumber;
	classvar pitchClassNumberToPitchClassName, pitchClassNumberToPitchClassNameWithFlats;
	classvar pitchClassNumberToPitchClassNameWithSharps, pitchNameToLilypondPitchName;
	classvar semitonesToAccidentalName;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*initClass {
		accidentalNameToLilypondAccidentalName = (
			'bb': 	"ff",
			'b~': 	"tqf",
			'b': 	"f",
			'~': 	"qf",
			'': 	"",
			'+': 	"qs",
			'#': 	"s",
			'#+': 	"tqs",
			'##': 	"ss"
		);
		accidentalNameToSemitones = (
			'ff':	-2.0,
			'ffr':	-1.75,
			'tqfl':	-1.75,
			'tqf':	-1.5,
			'tqfr':	-1.25,
			'fl':	-1.25,
			'f':	-1.0,
			'fr':	-0.75,
			'qfl':	-0.75,
			'qf':	-0.5,
			'qfr':	-0.25,
			'l': 	-0.25,
			'': 	0.0,
			'r': 	0.25,
			'qsl': 	0.25,
			'qs': 	0.5,
			'qsr': 	0.75,
			'sl': 	0.75,
			's': 	1.0,
			'sr':	1.25,
			'tqsl': 1.25,
			'tqs': 	1.5,
			'tqsr': 1.75,
			'ssl': 	1.75,
			'ss': 	2.0
		);
		// accidentalNameToSemitones = (
		// 	'bb':	-2.0,
		// 	'bbr':	-1.75,
		// 	'b~l':	-1.75,
		// 	'b~':	-1.5,
		// 	'b~r':	-1.25,
		// 	'bl':	-1.25,
		// 	'b':	-1.0,
		// 	'br':	-0.75,
		// 	'~l':	-0.75,
		// 	'~':	-0.5,
		// 	'~r':	-0.25,
		// 	'l': 	-0.25,
		// 	'': 	0.0,
		// 	'r': 	0.25,
		// 	'+l': 	0.25,
		// 	'+': 	0.5,
		// 	'+r': 	0.75,
		// 	'#l': 	0.75,
		// 	'#': 	1.0,
		// 	'#r':	1.25,
		// 	'#+l': 	1.25,
		// 	'#+': 	1.5,
		// 	'#+r': 	1.75,
		// 	'##l': 	1.75,
		// 	'##': 	2.0
		// );
		// accidentalNameToSemitones = (
		// 	'bb':	-2.0,
		// 	'b~':	-1.5,
		// 	'b':	-1.0,
		// 	'~':	-0.5,
		// 	'': 	0.0,
		// 	'+': 	0.5,
		// 	'#': 	1.0,
		// 	'#+': 	1.5,
		// 	'##': 	2.0
		// );
		diatonicPCNumberToDiatonicPitchClassNumber = (
			0: 	0,
			1: 	2,
			2: 	4,
			3: 	5,
			4: 	7,
			5: 	9,
			6: 	11
		);
		diatonicPitchClassNameToDiatonicPitchClassNumber = (
			'C': 	0, 
			'D': 	1, 
			'E': 	2, 
			'F': 	3, 
			'G': 	4, 
			'A': 	5, 
			'B': 	6
		);
		lilypondAccidentalNameToAccidentalName = (
			'ff': 	"bb",
			'tqf': 	"b~",
			'f': 	"b",
			'qf': 	"~",
			'': 	"",
			'qs': 	"+",
			's': 	"#",
			'tqs': 	"#+",
			'ss': 	"##"
		);
		pitchClassNameToDiatonicPitchClassNumber = (
			'C': 	0,
			'D': 	2,
			'E': 	4,
			'F': 	5,
			'G': 	7,
			'A': 	9,
			'B': 	11
		);
		// pitchClassNumberToPitchClassName = (
		// 	0.0: 	"C",
		// 	0.5: 	"C+",
		// 	1.0: 	"C#",
		// 	1.5: 	"D~",
		// 	2.0: 	"D",
		// 	2.5: 	"D+",
		// 	3.0: 	"Eb",
		// 	3.5: 	"E~",
		// 	4.0: 	"E",
		// 	4.5: 	"E+",
		// 	5.0: 	"F",
		// 	5.5: 	"F+",
		// 	6.0: 	"F#",
		// 	6.5: 	"G~",
		// 	7.0: 	"G",
		// 	7.5: 	"G+",
		// 	8.0: 	"Ab",
		// 	8.5: 	"A~",
		// 	9.0: 	"A",
		// 	9.5: 	"A+",
		// 	10.0: 	"Bb",
		// 	10.5: 	"B~",
		// 	11.0: 	"B",
		// 	11.5: 	"B+"
		// );
		pitchClassNumberToPitchClassName = (
			0.0: 	"C",
			0.25:	"C",
			0.5: 	"C+",
			0.75:	"C#",
			1.0: 	"C#",
			1.25:	"C#",
			1.5: 	"D~",
			1.75:	"D",
			2.0: 	"D",
			2.25:	"D",
			2.5: 	"D+",
			2.75:	"Eb",
			3.0: 	"Eb",
			3.25:	"Eb",
			3.5: 	"E~",
			3.75:	"E",
			4.0: 	"E",
			4.25:	"E",
			4.5: 	"E+",
			4.75: 	"F",
			5.0: 	"F",
			5.25: 	"F",
			5.5: 	"F+",
			5.75: 	"F#",
			6.0: 	"F#",
			6.25: 	"F#",
			6.5: 	"G~",
			6.75: 	"G",
			7.0: 	"G",
			7.25: 	"G",
			7.5: 	"G+",
			7.75: 	"Ab",
			8.0: 	"Ab",
			8.25: 	"Ab",
			8.5: 	"A~",
			8.75: 	"A",
			9.0: 	"A",
			9.25: 	"A",
			9.5: 	"A+",
			9.75: 	"Bb",
			10.0: 	"Bb",
			10.25: 	"Bb",
			10.5: 	"B~",
			10.75: 	"B",
			11.0: 	"B",
			11.25: 	"B",
			11.5: 	"B+"
		);
		//!!!TODO - update to include 1/8th tones
		pitchClassNumberToPitchClassNameWithFlats = (
			0.0: 	"C",
			0.5: 	"Db~",
			1.0: 	"Db",
			1.5: 	"D~",
			2.0: 	"D",
			2.5: 	"Eb~",
			3.0: 	"Eb",
			3.5: 	"E~",
			4.0: 	"E",
			4.5: 	"F~",
			5.0: 	"F",
			5.5: 	"Gb~",
			6.0: 	"Gb",
			6.5: 	"G~",
			7.0: 	"G",
			7.5: 	"Ab~",
			8.0: 	"Ab",
			8.5: 	"A~",
			9.0: 	"A",
			9.5: 	"Bb~",
			10.0: 	"Bb",
			10.5: 	"B~",
			11.0: 	"B",
			11.5: 	"C~"
		);
		//!!!TODO - update to include 1/8th tones
		pitchClassNumberToPitchClassNameWithSharps = (
			0.0: 	'C',
			0.5: 	'C+',
			1.0: 	'C#',
			1.5: 	'C#+',
			2.0: 	'D',
			2.5: 	'D+',
			3.0: 	'D#',
			3.5: 	'D#+',
			4.0: 	'E',
			4.5: 	'E+',
			5.0: 	'F',
			5.5: 	'F+',
			6.0: 	'F#',
			6.5: 	'F#+',
			7.0: 	'G',
			7.5: 	'G+',
			8.0: 	'G#',
			8.5: 	'G#+',
			9.0: 	'A',
			9.5: 	'A+',
			10.0: 	'A#',
			10.5: 	'A#+',
			11.0: 	'B',
			11.5: 	'B+'
		);
		semitonesToAccidentalName = (
			-2.0: 	"bb",
			-1.5: 	"b~",
			-1.0: 	"b",
			-0.5: 	"~",
			0.0: 	"",
			0.5: 	"+",
			1.0: 	"#",
			1.5: 	"#+",
			2.0: 	"##"
		);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.accidentalNameToLilypondAccidentalName('#+');
	-------------------------------------------------------------------------------------------------------- */
	*accidentalNameToLilypondAccidentalName { |accidentalName|
		^accidentalNameToLilypondAccidentalName[accidentalName.asSymbol];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.accidentalNameToSemitones('#+');
	-------------------------------------------------------------------------------------------------------- */
	*accidentalNameToSemitones { |accidentalName|
		^accidentalNameToSemitones[accidentalName.asSymbol];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.diatonicPCNumberToDiatonicPitchClassNumber(5);
	-------------------------------------------------------------------------------------------------------- */
	*diatonicPCNumberToDiatonicPitchClassNumber { |pcNumber|
		^diatonicPCNumberToDiatonicPitchClassNumber[pcNumber];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.diatonicPitchClassNameToDiatonicPitchClassNumber("G");
	-------------------------------------------------------------------------------------------------------- */
	*diatonicPitchClassNameToDiatonicPitchClassNumber { |pitchClassName|
		var name;
		name = pitchClassName.asString.diatonicPitchClassName;
		name = name.toUpper.asSymbol;
		^diatonicPitchClassNameToDiatonicPitchClassNumber[name];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.diatonicPitchClassNameToLilypondDiatonicPitchClassName("G");
	-------------------------------------------------------------------------------------------------------- */
	*diatonicPitchClassNameToLilypondDiatonicPitchClassName { |diatonicPitchClassName|
		^if (diatonicPitchClassName.isDiatonicPitchClassName) {
			diatonicPitchClassName.toLower;
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.lilypondAccidentalNameToAccidentalName('tqs');
	-------------------------------------------------------------------------------------------------------- */
	*lilypondAccidentalNameToAccidentalName { |lilypondAccidentalName|
		^lilypondAccidentalNameToAccidentalName[lilypondAccidentalName.asSymbol];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.lilypondDiatonicPitchClassNameToDiatonicPitchClassName("g");
	-------------------------------------------------------------------------------------------------------- */
	*lilypondDiatonicPitchClassNameToDiatonicPitchClassName { |lilypondDiatonicPitchClassName|

		^if (lilypondDiatonicPitchClassName.isLilypondDiatonicPitchClassName) {
			lilypondDiatonicPitchClassName.toUpper;
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.lilypondOctaveNumberToOctaveNumber("''");
	FoscPitchNameManager.lilypondOctaveNumberToOctaveNumber(",,,,");
	FoscPitchNameManager.lilypondOctaveNumberToOctaveNumber("");
	-------------------------------------------------------------------------------------------------------- */
	*lilypondOctaveNumberToOctaveNumber { |lilypondOctaveName|
		^if (lilypondOctaveName.isLilypondOctaveName) {
			case
			{ lilypondOctaveName.includes($,) } { 3 - lilypondOctaveName.size }
			{ lilypondOctaveName.includes($') } { 3 + lilypondOctaveName.size }
			{ lilypondOctaveName.isEmpty } { 3 }

		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.lilypondPitchNameToPitchName("gs''");
	FoscPitchNameManager.lilypondPitchNameToPitchName("ctqf");
	FoscPitchNameManager.lilypondPitchNameToPitchName("aff,,");
	-------------------------------------------------------------------------------------------------------- */
	*lilypondPitchNameToPitchName { |lilypondPitchName|
		^if (lilypondPitchName.isLilypondPitchName) {
			format(
				"%%%",
				this.lilypondDiatonicPitchClassNameToDiatonicPitchClassName(
					lilypondPitchName.lilypondDiatonicPitchClassName),
				this.lilypondAccidentalNameToAccidentalName(lilypondPitchName.lilypondAccidentalName),
				this.lilypondOctaveNumberToOctaveNumber(lilypondPitchName.lilypondOctaveName)
			);
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.octaveNumberToLilypondOctaveName(-1);
	FoscPitchNameManager.octaveNumberToLilypondOctaveName(5);
	-------------------------------------------------------------------------------------------------------- */
	*octaveNumberToLilypondOctaveName { |octaveNumber|
		if (octaveNumber.isString) { octaveNumber = octaveNumber.interpret };

		^if (octaveNumber.isInteger) {
			case
			{ octaveNumber < 3 } { ",".wrapExtend(3 - octaveNumber) }
			{ octaveNumber == 3 } { "" }
			{ octaveNumber > 3 } { "'".wrapExtend(octaveNumber - 3)  }
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchClassNameToDiatonicPitchClassNumber("C#");
	-------------------------------------------------------------------------------------------------------- */
	*pitchClassNameToDiatonicPitchClassNumber { |pitchClassName|
		var name;
		name = pitchClassName.asString.diatonicPitchClassName;
		name = name.toUpper.asSymbol;
		^pitchClassNameToDiatonicPitchClassNumber[name];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchClassNameToLilypondPitchClassName("Ab");
	-------------------------------------------------------------------------------------------------------- */
	*pitchClassNameToLilypondPitchClassName { |pitchClassName|
		^if (pitchClassName.isPitchClassName) {
			format(
				"%%",
				this.diatonicPitchClassNameToLilypondDiatonicPitchClassName(pitchClassName.diatonicPitchClassName),
				this.accidentalNameToLilypondAccidentalName(pitchClassName.accidentalName)
			);
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchClassNumberToPitchClassName(8);
	-------------------------------------------------------------------------------------------------------- */
	*pitchClassNumberToPitchClassName { |pitchClassNumber|
		^pitchClassNumberToPitchClassName[pitchClassNumber.round(0.25)];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchClassNumberToPitchClassNameWithFlats(8);
	-------------------------------------------------------------------------------------------------------- */
	*pitchClassNumberToPitchClassNameWithFlats { |pitchClassNumber|
		^pitchClassNumberToPitchClassNameWithFlats[pitchClassNumber.round(0.5)];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchClassNumberToPitchClassNameWithSharps(8);
	-------------------------------------------------------------------------------------------------------- */
	*pitchClassNumberToPitchClassNameWithSharps { |pitchClassNumber|
		^pitchClassNumberToPitchClassNameWithSharps[pitchClassNumber.round(0.5)];
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.pitchNameToLilypondPitchName("Ab4");

	FoscPitchNameManager.pitchNameToLilypondPitchName("Ab4", arrow: 'up');
	-------------------------------------------------------------------------------------------------------- */
	*pitchNameToLilypondPitchName { |pitchName, arrow|
		^if (pitchName.isPitchName) {
			if (arrow.notNil) {
				format(
					"%%%%",
					this.diatonicPitchClassNameToLilypondDiatonicPitchClassName(pitchName.diatonicPitchClassName),
					this.accidentalNameToLilypondAccidentalName(pitchName.accidentalName),
					switch(arrow, 'up', "r", 'down', "l"),
					this.octaveNumberToLilypondOctaveName(pitchName.octaveName)
				);
			} {
				format(
					"%%%",
					this.diatonicPitchClassNameToLilypondDiatonicPitchClassName(pitchName.diatonicPitchClassName),
					this.accidentalNameToLilypondAccidentalName(pitchName.accidentalName),
					this.octaveNumberToLilypondOctaveName(pitchName.octaveName)
				);
			};
		} {
			nil;
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	FoscPitchNameManager.semitonesToAccidentalName(-1.5);
	FoscPitchNameManager.semitonesToAccidentalName(2);
	-------------------------------------------------------------------------------------------------------- */
	*semitonesToAccidentalName { |semitones|
		^semitonesToAccidentalName[semitones.round(0.5)];
	}
}
