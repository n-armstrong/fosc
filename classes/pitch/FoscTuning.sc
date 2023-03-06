/* ------------------------------------------------------------------------------------------------------------
• FoscTuning

!!!TODO
- clean up and rename the 'illustrate' methods - these are confusing
- if the stylesheet is written automatically these can all be made private
- write a class for parsing tuning specifications from ekmelily stylesheets


• Example 1 - use a tuning from the FoscTuning library

t = FoscTuning.et72;
t.show; // examine the microtonal spellings and alterations


• Example 2 - create music that uses the tuning

Fosc.tuning_('et72');
a = FoscNote("cfts'", 1/4);
a.show;
a.eventList;


• Example 3 - create music that uses the tuning

Fosc.tuning_('et72');
m = FoscMusicMaker().(durations: 1/4 ! 4, pitches: "cetf' dfxf' estf' frs'");
m.show;
m.eventList.printAll; "";


• Example 4 - define a custom tuning

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

t.show;                 // examine the microtonal spellings and alterations

Fosc.tuning_('foo');    // make this tuning current for Fosc notation
a = FoscPitch("cfts'"); // create music that uses the tuning
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscTuning : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //classvar <current;
    var <name, <tuples, <alterations, <accidentalNames, <glyphNames, <stylesheetPath;
    *new { |name, tuples|
        ^super.new.init(name, tuples);
    }
    init { |argName, argTuples|
        var alteration, accidentalName;

        name = argName;
        tuples = argTuples;
        # alterations, accidentalNames, glyphNames = tuples.flop;
        
        //!!!TODO: should stylesheet writing be automatic?
        stylesheetPath = "%/%.ily".format(Fosc.stylesheetDirectory, name);
        //this.prIllustrateStylesheet.writeLY(stylesheetPath);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • alterations

    t = FoscTuning.et72;
    t.alterations;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • accidentalNames

    t = FoscTuning.et72;
    t.accidentalNames;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • glyphNames

    t = FoscTuning.et72;
    t.glyphNames;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    t = FoscTuning.et72;
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes, stretch|
        var currentTuning, accidentalTags, maker, score, pitchNames, music, markup, ratio, lilypondFile, contextBlock;

        currentTuning = Fosc.tuning;
        Fosc.tuning = this;
        accidentalTags = tuples.flop[1];
        maker = FoscMusicMaker();
        score = FoscScore();

        #["g"].do { |pitchName|
            pitchNames = accidentalTags.collect { |tag| "%%'".format(pitchName, tag) }.join(" ");
            pitchNames.postln;
            music = maker.(durations: 1/4 ! accidentalTags.size, pitches: pitchNames);
            
            // music.doLeaves { |leaf, i|
            //     ratio = (FoscAccidental(accidentalTags[i]).semitones / 2).asFraction;
            //     ratio = if (ratio[0] % ratio[1] == 0) { ratio[0].asString } { "%/%".format(*ratio) };
            //     markup = "^\\markup { \\center-align \\teeny \\center-column { % % } }";
            //     markup = markup.format(tuples[i][1], ratio);
            //     leaf.attach(FoscLilyPondLiteral(markup, 'after'));
            // };

            score.add(FoscStaff(music));
        };

        lilypondFile = score.illustrate(
            paperSize: paperSize,
            staffSize: staffSize,
            includes: includes ++ ["%/noteheads.ily".format(Fosc.stylesheetDirectory)]
        );

        contextBlock = FoscContextBlock(sourceLilypondType: 'Score');
        contextBlock.items.add("proportionalNotationDuration = #(ly:make-moment 1/8)");
        lilypondFile.layoutBlock.items.add(contextBlock);

        //Fosc.tuning = currentTuning;

        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • post

    t = FoscTuning.et48;
    t.post;
    -------------------------------------------------------------------------------------------------------- */
    post {
        var alteration, accidentalName;

        this.tuples.do { |tuple|
            # alteration, accidentalName = tuple;
            "%\t\t'%'".format(alteration, accidentalName).postln;
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS: TUNINGS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *et24

    t = FoscTuning.et24;
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    *et24 {
        ^FoscTuning('et24', [
            [-2,    "ff",   0xE264],
            [-1.5,  "tqf",  0xE281],
            [-1,    "f",    0xE260],
            [-0.5,  "qf",   0xE280],
            [0,     "",     0xE261],
            [0.5,   "qs",   0xE282],
            [1,     "s",    0xE262],
            [1.5,   "tqs",  0xE283],
            [2,     "ss",   0xE263]  
        ]);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *et48

    t = FoscTuning.et48;
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    *et48 {
        //!!! ignore unicode below -- stylesheet is not being written automatically
        ^FoscTuning('et48', [
            [-2,    "ff",   0xE264],
            [-1.75, "tqfl",  0xE261],
            [-1.5,  "tqf",  0xE281],
            [-1.25, "fl",  0xE261],
            [-1,    "f",    0xE260],
            [-0.75, "fr",  0xE261],
            [-0.5,  "qf",   0xE280],
            [-0.25, "l",   0xE261],
            [0,     "",     0xE261],
            [0.25,  "r",   0xE261],
            [0.5,   "qs",   0xE282],
            [0.75,  "sl",  0xE261],
            [1,     "s",    0xE262],
            [1.25,  "sr",  0xE261],
            [1.5,   "tqs",  0xE283],
            [1.75,  "tqsr",  0xE261],
            [2,     "ss",   0xE263]  
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
            [-2,    "ff",   0xE264],            // double-flat
            [-11/6, "etf",  0xED50, 0xE264],    // eleven-twelfhts-flat
            [-10/6,  "fxf", 0xE2C5],            // five-sixths-flat
            [-9/6,  "tqf",  0xE281],            // three-quarters-flat
            [-8/6,  "trf",  0xE271],            // two-thirds-flat
            [-7/6,  "stf",  0xED51, 0xE260],    // seven-twelfths-flat
            [-1,    "f",    0xE260],            // flat
            [-5/6,  "ftf",  0xED50, 0xE260],    // five-twelfths-flat
            [-4/6,  "rf",   0xE270],            // third-flat
            [-3/6,  "qf",   0xE280],            // quarter-flat
            [-2/6,  "xf",   0xE273],            // sixth-flat
            [-1/6,  "tf",   0xED51],            // twelfth-flat
            [0,     "",     0xE261],
            [1/6,   "ts",   0xED50],            // twelfth-sharp
            [2/6,   "xs",   0xE272],            // sixth-sharp
            [3/6,   "qs",   0xE282],            // quarter-sharp
            [4/6,   "rs",   0xE275],            // third-sharp
            [5/6,   "fts",  0xED51, 0xE262],    // five-twelfths-sharp
            [1,     "s",    0xE262],            // sharp
            [7/6,   "sts",  0xED50, 0xE262],    // seven-twelfths-sharp
            [8/6,   "trs",  0xE274],            // two-thirds-sharp
            [9/6,   "tqs",  0xE283],            // three-quarters-sharp
            [10/6,  "fxs",  0xE2C4],            // five-sixths-sharp
            [11/6,  "ets",  0xED51, 0xE263],    // eleven-twelfths-sharp
            [2,     "ss",   0xE263]             // double-sharp
        ]);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFormatStyleDefinitionBlock
    -------------------------------------------------------------------------------------------------------- */
    prFormatStyleDefinitionBlock {
        var result, str, ratio, glyphHexString;

        result = [];

        tuples.do { |list, i|
            ratio = this.prLilypondAlterationStrings[i];
            //glyphHexString = "#x%".format(list[2].asHexString[4..]);
            glyphHexString = list[2..].collect { |char| "#x%".format(char.asHexString[4..]) }.join($ );
            str = "(% %)".format(ratio, glyphHexString);
            result = result.add(str);
        };

        result = result.join("\n\t");
        result = "\\ekmelicUserStyle \"%\" #'(\n\t%\n)".format(name, result);

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatStylesheetHeader
    -------------------------------------------------------------------------------------------------------- */
    prFormatStylesheetHeader {
        var result;

        result = FoscLilyPondLiteral(#["ekmelicFont = \"Bravura\""]);
        result = result.prGetFormatPieces.join("\n");

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
    }/* --------------------------------------------------------------------------------------------------------
    • prIllustrateStylesheet
    -------------------------------------------------------------------------------------------------------- */
    prIllustrateStylesheet {
        var lilypondFile;

        lilypondFile = FoscLilyPondFile(
            lilypondLanguageToken: "english",
            lilypondVersionToken: Fosc.lilypondVersion,
            includes: ["ekmel.ily"],
            isStylesheet: true
        );
        
        lilypondFile.items.add(this.prFormatStylesheetHeader);
        lilypondFile.items.add(this.prFormatPitchNamesBlock);
        lilypondFile.items.add(this.prFormatStyleDefinitionBlock);

        ^lilypondFile;
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
