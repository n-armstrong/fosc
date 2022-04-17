/* ------------------------------------------------------------------------------------------------------------
• FoscTuning

!!!TODO
- clean up and rename the 'illustrate' methods - these are confusing
- if the stylesheet is written automatically, these can all be made private

!!!TODO
- write a class for parsing tuning specifications from ekmelily stylesheets


• Example 1 - use a tuning from the FoscTuning library

t = FoscTuning.et72;
t.show; // examine the microtonal spellings and alterations
FoscConfiguration.tuning_(t); // make this tuning current for Fosc notation

// create music that uses the tuning
a = FoscPitch("cfts'");
a.show;


• Example 2 - specify a custom tuning

t = FoscTuning('foo', [
    [-3/2, "tqf", 0xE281],
    [-4/3, "trf", 0xE2CB],
    [-7/6, "stf", 0xE2C1],
    [-1, "f", 0xE260],
    [-5/6, "ftf", 0xE2C6],
    [-2/3, "rf", 0xE2D0],
    [-1/2, "qf", 0xE280],
    [-1/3, "xf", 0xE2CC],
    [-1/6, "tf", 0xE2C2],
    [0, "", 0xE261],
    [1/6, "ts", 0xE2C7],
    [1/3, "xs", 0xE2D1],
    [1/2, "qs", 0xE282],
    [2/3, "rs",0xE2CD],
    [5/6, "fts", 0xE2C3],
    [1, "s", 0xE262],
    [7/6, "sts", 0xE2C8],
    [4/3, "trs", 0xE2D2],
    [3/2, "tqs", 0xE283],
]);

t.show; // examine the microtonal spellings and alterations
FoscConfiguration.tuning_(t); // make this tuning current for Fosc notation

// create music that uses the tuning
a = FoscPitch("cfts'");
a.show;

FoscTuning.current.show;
FoscTuning.current.accidentalNames.includes("");
------------------------------------------------------------------------------------------------------------ */
FoscTuning : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <current;
    var <name, <tuples, <alterations, <accidentalNames, <glyphNames;
    *new { |name, tuples|
        ^super.new.init(name, tuples);
    }
    init { |argName, argTuples|
        var alteration, accidentalName;

        name = argName;
        tuples = argTuples;
        # alterations, accidentalNames, glyphNames = tuples.flop;
        
        //!!!TODO: should this be written automatically?
        //this.writeStylesheet;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *initClass
    -------------------------------------------------------------------------------------------------------- */
    *initClass {
        StartUp.add {
            this.addDependant(FoscPitchManager);
            this.current_(this.et24);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • alterations

    FoscTuning.current.alterations;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • accidentalNames

    FoscTuning.current.accidentalNames;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • glyphNames

    FoscTuning.current.glyphNames;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    t = FoscTuning.et72;
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        var lilypondFile, result, accidentalTags, str, markup, ratio;

        lilypondFile = FoscLilypondFile(
            lilypondLanguageToken: "english",
            lilypondVersionToken: "2.20.0",
            includes: ["%/noteheads.ily".format(FoscConfiguration.stylesheetDirectory)]
        );

        result = [];

        accidentalTags = tuples.flop[1];

        #[c,d,e,f,g,a,b].do { |pitchName, i|
            result = result.add("\\new Staff {");
            
            accidentalTags.do { |tag, j|
                str = "\t%%'4".format(pitchName, tag);
                markup = "% ^\\markup { \\center-align \\teeny \\center-column { % % } }";
                ratio = this.prLilypondAlterationStrings[j];
                str = markup.format(str, tuples[j][1], ratio);
                result = result.add(str);
            };
            
            result = result.add("}");
        };

        result = result.join("\n");
        
        lilypondFile.items.add(result);

        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrateStylesheet
    -------------------------------------------------------------------------------------------------------- */
    illustrateStylesheet {
        var lilypondFile;

        lilypondFile = FoscLilypondFile(
            lilypondLanguageToken: "english",
            lilypondVersionToken: FoscConfiguration.lilypondVersionString,
            includes: ["ekmel.ily"],
            isStylesheet: true
        );
        
        lilypondFile.items.add(this.prFormatStylesheetHeader);
        lilypondFile.items.add(this.prFormatPitchNamesBlock);
        lilypondFile.items.add(this.prFormatStyleDefinitionBlock);

        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • writeStylesheet

    !!!TODO: rename: writeStylesheet ?
    -------------------------------------------------------------------------------------------------------- */
    writeStylesheet {
        var path, lilypondFile;

        path = FoscConfiguration.stylesheetDirectory;
        lilypondFile = this.illustrateStylesheet;
        lilypondFile.writeLY("%/%.ily".format(path, name));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS: TUNINGS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *current_

    FoscTuning.current_('default');
    FoscTuning.current;
    -------------------------------------------------------------------------------------------------------- */
    *current_ { |tuning|
        if (tuning.isString) { tuning = tuning.asSymbol };

        if (tuning.isKindOf(Symbol)) { tuning = FoscTuning.perform(tuning) };

        if (tuning.isKindOf(FoscTuning).not) {
            throw("Bad argument for %:%: %.".format(this.species, thisMethod.name, tuning));
        };

        current = tuning;
        this.changed; // broadcast to FoscPitchManager
    }
    /* --------------------------------------------------------------------------------------------------------
    • *default

    t = FoscTuning.default;
    t.show;                         // examine the microtonal spellings and alterations
    FoscConfiguration.tuning_(t);   // make this tuning current for Fosc notation

    a = FoscLeafMaker().((60, 60.5 .. 72), [1/4]);
    a = FoscPitchSegment(a);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *default {
        ^FoscTuning('default', this.et24.tuples);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *et24

    t = FoscTuning.et24;
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    *et24 {
        ^FoscTuning('et24', [
            [-2, "ff", 0xE264],
            [-1.5, "tqf", 0xE281],
            [-1, "f", 0xE260],
            [-0.5, "qf", 0xE280],
            [0, "", 0xE261],
            [0.5, "qs", 0xE282],
            [1, "s", 0xE262],
            [1.5, "tqs", 0xE283],
            [2, "ss", 0xE263]  
        ]);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *et72

    t = FoscTuning.et72;
    t.show;

    ((0, 1/6 .. 4) - 2).do { |val| "%/%".format(*val.asFraction).postln };

    r: third
    q: quarter
    x: sixth
    t: twelfth
    -------------------------------------------------------------------------------------------------------- */
    *et72 {
        ^FoscTuning('et72', [
            [-2, "ff", 0xE264],       // double-flat
            [-11/6, "etf", 0xE2C5],   // eleven-twelfhts-flat
            [-5/3, "fxf", 0xE2CF],    // five-sixths-flat
            [-3/2, "tqf", 0xE281],    // three-quarters-flat
            [-4/3, "trf", 0xE2CB],    // two-thirds-flat
            [-7/6, "stf", 0xE2C1],    // seven-twelfths-flat
            [-1, "f", 0xE260],        // flat
            [-5/6, "ftf", 0xE2C6],    // five-twelfths-flat
            [-2/3, "rf", 0xE2D0],     // third-flat
            [-1/2, "qf", 0xE280],     // quarter-flat
            [-1/3, "xf", 0xE2CC],     // sixth-flat
            [-1/6, "tf", 0xE2C2],     // twelfth-flat
            [0, "", 0xE261],
            [1/6, "ts", 0xE2C7],      // twelfth-sharp
            [1/3, "xs", 0xE2D1],      // sixth-sharp
            [1/2, "qs", 0xE282],      // quarter-sharp
            [2/3, "rs",0xE2CD],       // third-sharp
            [5/6, "fts", 0xE2C3],     // five-twelfths-sharp
            [1, "s", 0xE262],         // sharp
            [7/6, "sts", 0xE2C8],     // seven-twelfths-sharp
            [4/3, "trs", 0xE2D2],     // two-thirds-sharp
            [3/2, "tqs", 0xE283],     // three-quarters-sharp
            [5/3, "fxs", 0xE2CE],     // five-sixths-sharp
            [11/6, "ets", 0xE2C4],    // eleven-twelfths-sharp
            [2, "ss", 0xE263]         // double-sharp
        ]);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFormatStylesheetHeader
    -------------------------------------------------------------------------------------------------------- */
    prFormatStylesheetHeader {
        var result;

        result = FoscLilypondLiteral(#["ekmelicFont = \"Bravura\""]);
        result = result.prGetFormatPieces.join("\n");

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatStyleDefinitionBlock
    -------------------------------------------------------------------------------------------------------- */
    prFormatStyleDefinitionBlock {
        var result, str, ratio, glyphHexString;

        result = [];

        tuples.do { |list, i|
            ratio = this.prLilypondAlterationStrings[i];
            glyphHexString = "#x%".format(list[2].asHexString[4..]);
            str = "(% %)".format(ratio, glyphHexString);
            result = result.add(str);
        };

        result = result.join("\n\t");
        result = "\\ekmelicUserStyle \"%\" #'(\n\t%\n)".format(name, result);

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatPitchNamePieces
    -------------------------------------------------------------------------------------------------------- */
    prFormatPitchNamesBlock {
        var result, str, ratio;

        result = [];

        //!!! update to use instance variable directly, rather than tuples var
        tuples.do { |list, i|
            ratio = this.prLilypondAlterationStrings[i];

            #[c,d,e,f,g,a,b].do { |pitchName, j|
                str = "(%% . ,(ly:make-pitch -1 % %))".format(pitchName, list[1], j, ratio);
                result = result.add(str);
            };
        };

        result = result.join("\n\t");
        result = "pitchNames = #`(\n\t%\n)".format(result);

        result = result ++ "\n\n";
        result = result ++ "pitchnames = \\pitchNames";
        result = result ++ "\n#(ly:parser-set-note-names pitchNames)";

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prLilypondAlterationStrings

    t = FoscTuning.et72;
    t.prLilypondAlterationStrings;
    -------------------------------------------------------------------------------------------------------- */
    prLilypondAlterationStrings {
        var result, str;

        result = [];

        this.alterations.do { |ratio|
            ratio = ratio * 0.5;
            str = if (ratio.frac != 0) { ratio.asFraction.join("/") } { ratio.asInteger.asString };
            result = result.add(str);
        };

        ^result;
    }

}
