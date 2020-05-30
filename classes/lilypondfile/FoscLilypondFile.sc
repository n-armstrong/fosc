/* ------------------------------------------------------------------------------------------------------------
• FoscLilypondFile


A Lilypond file.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
f = FoscLilypondFile(a);
f.show;


• Example 2: Set proportional notation duration.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
f = FoscLilypondFile(a);
b = f.layoutBlock['Score'];
if (b.isNil) {
    b = FoscContextBlock(sourceLilypondType: 'Score');
    f.layoutBlock.items.add(b);
};
set(b).proportionalNotationDuration = FoscSchemeMoment([1, 20]);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscLilypondFile : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • !!!TODO: add 'tag' argument
    -------------------------------------------------------------------------------------------------------- */
    var <comments, <dateTimeToken, <defaultPaperSize, <globalStaffSize, <includes, <items, <tag;
    var <lilypondLanguageToken, <lilypondVersionToken, <useRelativeIncludes;
    var <headerBlock, <layoutBlock, <paperBlock, <scoreBlock; //!!!TODO: REMOVE THESE ?
    *new { |items, dateTimeToken, defaultPaperSize, comments, includes, globalStaffSize,
        lilypondLanguageToken, lilypondVersionToken, useRelativeIncludes=false|     
        ^super.new.init(items, dateTimeToken, defaultPaperSize, comments, includes, globalStaffSize,
            lilypondLanguageToken, lilypondVersionToken, useRelativeIncludes);
    }
    init { |argItems, argDateTimeToken, argDefaultPaperSize, argComments, argIncludes, argGlobalStaffSize,
        argLilypondLanguageToken, argLilypondVersionToken, argUseRelativeIncludes=false|
        items = List[
            FoscBlock(name: 'header'),
            FoscBlock(name: 'layout'),
            FoscBlock(name: 'paper'),
            FoscBlock(name: 'score')
        ];
        # headerBlock, layoutBlock, paperBlock, scoreBlock = items;
        if (argItems.notNil) { scoreBlock.items.add(argItems) };
        // items = List[argItems] ?? { List[] };
        if (dateTimeToken.isNil) { dateTimeToken = FoscDateTimeToken() };
        comments = argComments ? [];
        if (comments.notEmpty) { comments = comments.collect { |each| each.asString } };
        defaultPaperSize = argDefaultPaperSize;
        includes = argIncludes ? [];
        if (includes.notEmpty) { includes = includes.collect { |each| each.asString } };
        globalStaffSize = argGlobalStaffSize;
        lilypondLanguageToken = argLilypondLanguageToken;
        lilypondLanguageToken = lilypondLanguageToken ?? { FoscLilypondLanguageToken() };
        lilypondLanguageToken = FoscLilypondLanguageToken(lilypondLanguageToken);
        lilypondVersionToken = argLilypondVersionToken;
        lilypondVersionToken = lilypondVersionToken ?? { FoscLilypondVersionToken() };
        lilypondVersionToken = FoscLilypondVersionToken(lilypondVersionToken);
        useRelativeIncludes = argUseRelativeIncludes;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *pitch

    One staff per array in 'pitches'.

    a = (60,60.25..71);
    b = (60..53);
    f = FoscLilypondFile.pitch([a, b]);
    f.show;
    -------------------------------------------------------------------------------------------------------- */
    *pitch { |pitches, defaultPaperSize=#['a4', 'portrait'], globalStaffSize=16|
        var staves, staff, score, lilypondFile, block, scheme, moment;
        // if pitches.rank == 1 pitches = [[pitches]]
        staves = [];
        
        pitches.do { |each|
            // assert every in each isKindOf FoscPitch
            //each.do { |p| p.str.postln };
            staff = FoscStaff(FoscLeafMaker().(each, [1/4]));
            staves = staves.add(staff);
        };

        score = FoscScore(staves);

        //score.illustrate.format.postln;
        
        lilypondFile = this.new(
            score,
            defaultPaperSize: defaultPaperSize,
            globalStaffSize: globalStaffSize,
            includes: #[
                "../stylesheets/default.ily",
                "../stylesheets/microtonal-accidentals.ily"
            ],
            useRelativeIncludes: true
        );
        
        //!!!TODO: isn't all the stuff below set in the stylesheet?
        lilypondFile.headerBlock.tagline = false;
        lilypondFile.paperBlock.leftMargin = 20;
        lilypondFile.paperBlock.systemSystemSpacing = FoscSpacingVector(0, 0, 12, 0);
        lilypondFile.layoutBlock.indent = 0;
        lilypondFile.layoutBlock.raggedRight = true;
        lilypondFile.layoutBlock.items.add(FoscLilypondLiteral("\\accidentalStyle dodecaphonic"));
        
        // voice
        block = FoscContextBlock(sourceLilypondType: 'Voice');
        lilypondFile.layoutBlock.items.add(block);
        block.removeCommands.add('Stem_engraver');

        // staff
        block = FoscContextBlock(sourceLilypondType: 'Staff');
        lilypondFile.layoutBlock.items.add(block);
        block.removeCommands.add('Bar_engraver');
        block.removeCommands.add('Bar_number_engraver');
        block.removeCommands.add('Time_signature_engraver');
        
        // score
        block = FoscContextBlock(sourceLilypondType: 'Score');
        lilypondFile.layoutBlock.items.add(block);
        override(block).spacingSpanner.strictGraceSpacing = true;
        override(block).spacingSpanner.strictNoteSpacing = true;
        override(block).spacingSpanner.uniformStretching = true;
        moment = FoscSchemeMoment(1, 6);
        set(block).proportionalNotationDuration = moment;

        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *rhythm

    Makes rhythm-maker-style LilyPond file.
    
    Used in rhythm-maker docs.
    
    Returns LilyPond file.
    

    • Example 1

    Make a rhythmic staff.

    d = #[[3,4],[4,8],[1,4]];
    m = FoscLeafMaker();
    
    x = [m.(64 ! 6, 1/8), m.(64 ! 8, 1/16), m.(64 ! 2, 1/8)];
    x.do { |each| each.beam };

    f = FoscLilypondFile.rhythm(x, d);
    f.show;
    

    !!!TODO: functionality removed - bring it back?
    • Example 2

    Set time signatures explicitly.

    d = #[[3,4],[4,8],[1,4]];
    m = FoscLeafMaker();
    
    x = [m.(64 ! 6, 1/8), m.(64 ! 8, 1/16), m.(64 ! 2, 1/8)];
    x.do { |each| each.beam };

    t = #[[6,8],[4,8],[2,8]];
    f = FoscLilypondFile.rhythm(x, t);
    f.show;


    • Example 3

    Make pitched staff.

    d = #[[3,4],[4,8],[1,4]];
    m = FoscLeafMaker();
    
    x = [m.(64 ! 6, 1/8), m.(64 ! 8, 1/16), m.(64 ! 2, 1/8)];
    x.do { |each| each.beam };

    f = FoscLilypondFile.rhythm(x, d, pitchedStaff: true);
    f.show;


    • Example 4

    Adjust horizontal spacing with 'stretch'. Defaults to 1.


    m = FoscRhythmMaker().(divisions: [1/4], ratios: #[[2,1],[3,2],[4,3]]);
    f = FoscLilypondFile.rhythm(m, stretch: 4);
    f.show;


    • Example 5 !!!TODO: not yet working

    Make simultaneous voices.

    d = #[[3,4],[4,8],[1,4]];
    m = FoscLeafMaker();

    x = [m.(64 ! 6, 1/8), m.(64 ! 8, 1/16), m.(64 ! 2, 1/8)];
    x.do { |each| each.beam };
    x = FoscSelection(x).flat;

    y = [m.(60 ! 12, 1/16), m.(60 ! 16, 1/32), m.(60 ! 4, 1/16)];
    y.do { |each| each.beam };
    y = FoscSelection(y).flat;

    a = ('Voice_1': x, 'Voice_2': y);
    f = FoscLilypondFile.rhythm(a, divisions: d, attachLilypondVoiceCommands: true);

    f.show;


    • Example 6

    Use implicitly when displaying state of rhythm-makers.

    a = FoscRhythmMaker();
    a.(divisions: [1/4], ratios: #[[2,1],[3,2],[4,3]]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *rhythm { |selections, divisions, attachLilypondVoiceCommands=false, pitchedStaff=false|
        var score, multiplier, lilypondFile, selections_, staff, voices, voice, commandString;
        var voiceNameToCommandString, command, duration, message, context, skips, skip, timeSignatures;

        case
        { selections.isSequenceableCollection } {
            selections.do { |each| 
                if (each.isKindOf(FoscSelection).not) {
                    throw("%:%: must be a selection: %".format(this.species, thisMethod.name, each));
                };
            };
        }
        { selections.isKindOf(Dictionary) } {
            selections.values.do { |each| 
                if (each.isKindOf(FoscSelection).not) {
                    throw("%:%: must be a selection: %".format(this.species, thisMethod.name, each));
                };
            };
        }
        { selections.isKindOf(FoscSelection) } {
            ^this.rhythm([selections], divisions, attachLilypondVoiceCommands, pitchedStaff);
        }
        {
            throw("%:%: must be a SequenceableCollection or Dictionary: %"
                .format(this.species, thisMethod.name, selections)); 
        };

        score = FoscScore();
        //multiplier = FoscMultiplier(#[1,32]) * stretch.reciprocal;
        //set(score).proportionalNotationDuration = FoscSchemeMoment(multiplier.pair);

        lilypondFile = FoscLilypondFile(
            score,
            includes: [
                "default.ily",
                "rhythm-maker-docs.ily"
            ].collect { |each| "%/%".format(FoscConfiguration.foscStylesheetDirectory, each) },
            useRelativeIncludes: true
        );

        if (pitchedStaff.isNil) {
            case
            { selections.isSequenceableCollection } {
                selections_ = selections;
            }
            { selections.isKindOf(Dictionary) } {
                selections_ = selections.values;
            };
            block { |break|
                FoscIteration(selections_).leaves(FoscNote).do { |note|
                    if (note.writtenPitch != FoscPitch(60)) {
                        pitchedStaff = true;
                        break.value;
                    };
                };
            };
        };

        case
        { selections.isSequenceableCollection } {
            if (divisions.isNil) {
                duration = selections.collect { |each| each.duration }.sum;
                divisions = [duration];
            };
            if (pitchedStaff) {
                staff = FoscStaff();
            } {
                staff = FoscStaff(lilypondType: 'RhythmicStaff');
            };
            staff.addAll(selections);
        }
        { selections.isKindOf(Dictionary) } {
            voices = [];
            selections.keys.as(Array).sort.do { |voiceName|
                selections_ = selections[voiceName];
                selections_ = selections_.flat;
                selections_ = selections_.deepCopy;
                voice = FoscVoice(selections_, name: voiceName);
                if (attachLilypondVoiceCommands) {
                    voiceNameToCommandString = (
                        'Voice_1': "voiceOne",
                        'Voice_2': "voiceTwo",
                        'Voice_3': "voiceThree",
                        'Voice_4': "voiceFour"
                    );
                    commandString = voiceNameToCommandString[voiceName];
                    if (commandString.notNil) {
                        command = FoscLilypondLiteral("\\" ++ commandString);
                        voice.leafAt(0).attach(command);
                    };
                };
                voices.add(voice);
            };
            staff = FoscStaff(voices, isSimultaneous: true);
            if (divisions.isNil) {
                duration = staff.prGetDuration;
                divisions = [duration];
            };
        }
        {
            throw("%:%: must be SequenceableCollection or Dictionary of selections: %"
                .format(this.species, thisMethod.name, selections));
        };

        score.add(staff);
        assert(divisions.isSequenceableCollection);

        if (timeSignatures.isNil) {
            timeSignatures = divisions.collect { |fraction|
                fraction = FoscNonreducedFraction(fraction);
                if (#[1,2].includes(fraction.denominator)) { fraction = fraction.withDenominator(4) };
                fraction;
            };
        };
        
        timeSignatures = timeSignatures.collect { |each| FoscTimeSignature(each) };

        score.doComponents({ |staff, i|
            duration = staff.prGetContentsDuration;
            if (#[1,2].includes(duration.denominator)) { duration = duration.withDenominator(4) };
            staff.leafAt(0).attach(FoscTimeSignature(duration.pair));
        }, prototype: FoscStaff);
        
        ^lilypondFile;
    }
    // *rhythm { |selections, divisions, attachLilypondVoiceCommands=false, pitchedStaff=false, stretch=1|
    //     var score, multiplier, lilypondFile, selections_, staff, voices, voice, commandString;
    //     var voiceNameToCommandString, command, duration, message, context, skips, skip, timeSignatures;

    //     case
    //     { selections.isSequenceableCollection } {
    //         selections.do { |each| 
    //             if (each.isKindOf(FoscSelection).not) {
    //                 throw("%:%: must be a selection: %".format(this.species, thisMethod.name, each));
    //             };
    //         };
    //     }
    //     { selections.isKindOf(Dictionary) } {
    //         selections.values.do { |each| 
    //             if (each.isKindOf(FoscSelection).not) {
    //                 throw("%:%: must be a selection: %".format(this.species, thisMethod.name, each));
    //             };
    //         };
    //     }
    //     { selections.isKindOf(FoscSelection) } {
    //         ^this.rhythm([selections], divisions, attachLilypondVoiceCommands, pitchedStaff, stretch);
    //     }
    //     {
    //         throw("%:%: must be a SequenceableCollection or Dictionary: %"
    //             .format(this.species, thisMethod.name, selections)); 
    //     };

    //     score = FoscScore();
    //     multiplier = FoscMultiplier(#[1,32]) * stretch.reciprocal;
    //     set(score).proportionalNotationDuration = FoscSchemeMoment(multiplier.pair);

    //     lilypondFile = FoscLilypondFile(
    //         score,
    //         includes: [
    //             "default.ily",
    //             "rhythm-maker-docs.ily"
    //         ].collect { |each| "%/%".format(FoscConfiguration.foscStylesheetDirectory, each) },
    //         useRelativeIncludes: true
    //     );

    //     if (pitchedStaff.isNil) {
    //         case
    //         { selections.isSequenceableCollection } {
    //             selections_ = selections;
    //         }
    //         { selections.isKindOf(Dictionary) } {
    //             selections_ = selections.values;
    //         };
    //         block { |break|
    //             FoscIteration(selections_).leaves(FoscNote).do { |note|
    //                 if (note.writtenPitch != FoscPitch(60)) {
    //                     pitchedStaff = true;
    //                     break.value;
    //                 };
    //             };
    //         };
    //     };

    //     case
    //     { selections.isSequenceableCollection } {
    //         if (divisions.isNil) {
    //             duration = selections.collect { |each| each.duration }.sum;
    //             divisions = [duration];
    //         };
    //         if (pitchedStaff) {
    //             staff = FoscStaff();
    //         } {
    //             staff = FoscStaff(lilypondType: 'RhythmicStaff');
    //         };
    //         staff.addAll(selections);
    //     }
    //     { selections.isKindOf(Dictionary) } {
    //         voices = [];
    //         selections.keys.as(Array).sort.do { |voiceName|
    //             selections_ = selections[voiceName];
    //             selections_ = selections_.flat;
    //             selections_ = selections_.deepCopy;
    //             voice = FoscVoice(selections_, name: voiceName);
    //             if (attachLilypondVoiceCommands) {
    //                 voiceNameToCommandString = (
    //                     'Voice_1': "voiceOne",
    //                     'Voice_2': "voiceTwo",
    //                     'Voice_3': "voiceThree",
    //                     'Voice_4': "voiceFour"
    //                 );
    //                 commandString = voiceNameToCommandString[voiceName];
    //                 if (commandString.notNil) {
    //                     command = FoscLilypondLiteral("\\" ++ commandString);
    //                     voice.leafAt(0).attach(command);
    //                 };
    //             };
    //             voices.add(voice);
    //         };
    //         staff = FoscStaff(voices, isSimultaneous: true);
    //         if (divisions.isNil) {
    //             duration = staff.prGetDuration;
    //             divisions = [duration];
    //         };
    //     }
    //     {
    //         throw("%:%: must be SequenceableCollection or Dictionary of selections: %"
    //             .format(this.species, thisMethod.name, selections));
    //     };

    //     score.add(staff);
    //     assert(divisions.isSequenceableCollection);

    //     if (timeSignatures.isNil) {
    //         timeSignatures = divisions.collect { |fraction|
    //             fraction = FoscNonreducedFraction(fraction);
    //             if (#[1,2].includes(fraction.denominator)) { fraction = fraction.withDenominator(4) };
    //             fraction;
    //         };
    //     };
        
    //     timeSignatures = timeSignatures.collect { |each| FoscTimeSignature(each) };
    //     context = FoscContext(lilypondType: 'GlobalContext');
    //     skips = [];
    //     timeSignatures.do { |timeSignature|
    //         skip = FoscSkip(1);
    //         skip.multiplier_(timeSignature);
    //         skip.attach(timeSignature, context: 'Score');
    //         skips = skips.addAll(skip);
    //     };
    //     context.addAll(skips);
    //     score.insert(0, context);
    //     ^lilypondFile;
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • comments
    
    Gets comments of Lilypond file.
    
    a = FoscLilypondFile();
    a.comments;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • dateTimeToken
    
    Gets date-time token.
    
    a = FoscLilypondFile();
    a.dateTimeToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • defaultPaperSize

    Gets default paper size of Lilypond file. Set to pair or nil. Defaults to nil.
    
    a = FoscLilypondFile();
    a.defaultPaperSize;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • globalStaffSize

    Gets global staff size of Lilypond file. Set to number or nil. Defaults to nil.
    
    a = FoscLilypondFile();
    a.globalStaffSize;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • headerBlock

    Gets header block.

    Returns block or nil.
    
    a = FoscLilypondFile();
    a.headerBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // headerBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'header' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • includes

    Gets includes of Lilypond file.
    
    a = FoscLilypondFile();
    a.includes;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • items

    Gets items in Lilypond file.
    
    a = FoscLilypondFile();
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • layoutBlock

    Gets layout block.

    Returns block or nil.

    
    a = FoscLilypondFile();
    a.layoutBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // layoutBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'layout' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • lilypondLanguageToken

    Gets Lilypond language token.
    
    a = FoscLilypondFile();
    a.lilypondLanguageToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lilypondVersionToken

    Gets Lilypond version token.

    a = FoscLilypondFile();
    a.lilypondVersionToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • paperBlock

    Gets paper block.

    Returns block or nil.

    
    a = FoscLilypondFile();
    a.paperBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // paperBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'paper' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • scoreBlock

    Gets score block.

    Returns block or nil.
    

    a = FoscLilypondFile();
    a.scoreBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // scoreBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'score' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • useRelativeIncludes

    Is true when Lilypond file should use relative includes.

    a = FoscLilypondFile();
    a.useRelativeIncludes;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats Lilypond file.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getitem__)
    
    Gets item with name.

    Returns item.

    Raises key error when no item with name is found.
    -------------------------------------------------------------------------------------------------------- */
    at { |name|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate
    
    Illustrates Lilypond file.

    Returns Lilypond file unchanged.
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation of Lilypond file.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    c = ["File construct as an example.", "Second comment."];
    i = ["external-settings-file-1.ly", "external-settings-file-2.ly"];
    a = FoscLilypondFile(comments: c, includes: i, globalStaffSize: 16);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces { |tag|
        var result, includes, string;
        result = [];
        includes = [];
        //!!! DEPRECATED
        // if (dateTimeToken.notNil) {
        //     string = "\\% %".format(dateTimeToken.format);
        //     result = result.add(string);
        // };
        result = result.addAll(this.prGetFormattedComments);
        if (lilypondVersionToken.notNil) {
            string = "%".format(lilypondVersionToken.format);
            includes = includes.add(string);
        };
        if (lilypondLanguageToken.notNil) {
            string = "%".format(lilypondLanguageToken.format);
            includes = includes.add(string);
        };
        // if self.tag is not None:
        //     includes = LilyPondFormatManager.tag(
        //         includes,
        //         tag=self.tag,
        //         )
        includes = includes.join("\n");
        if (includes.notEmpty) {
            result = result.add(includes);
        };
        // postincludes = []
        if (useRelativeIncludes) {
            string = "#(ly:set-option 'relative-includes #t)";
            result = result.add(string);
            // postincludes.append(string)
        };
        result = result.addAll(this.prGetFormattedIncludes);
        result = result.addAll(this.prGetFormattedSchemeSettings);
        // result.extend(postincludes)
        result = result.addAll(this.prGetFormattedBlocks);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedBlocks

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    f = FoscLilypondFile(a);
    f.prGetFormattedBlocks;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedBlocks {
        var result, string;
        result = [];
        items.do { |item|
            case 
            { item.isKindOf(FoscBlock) && { item.items.isEmpty } } {
                // pass
            }
            { item.respondsTo('prGetLilypondFormat') && { item.isString.not } } {
                try {
                    string = item.prGetLilypondFormat;
                    //!!!TODO: string = item.prGetLilypondFormat(tag: tag);
                } {
                    string = item.prGetLilypondFormat;
                };
                if (string.notNil) { result = result.add(string) };
            }
            {
                result = result.add(item.asString);
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
   • prGetFormattedComments

    c = ["File construct as an example.", "A second comment on a new line."];
    a = FoscLilypondFile(comments: c);
    a.prGetFormattedComments;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedComments {
        var result, lilypondFormat;
        result = [];
        comments.do { |comment|
            if (comment.respondsTo('prGetLilypondFormat') && { comment.isString.not }) {
                lilypondFormat = comment.format;
                //"lilypondFormat".postln; lilypondFormat.postln;
                if (lilypondFormat.notEmpty) { result = result.add("\\% %".format(comment)) };
            } {
                result = result.add("\\% %".format(comment));
            };
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedIncludes
    
    i = [
        "external-settings-file-1.ly",
        "external-settings-file-2.ly"
    ];
    a = FoscLilypondFile(includes: i);
    a.prGetFormattedIncludes;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedIncludes {
        var result, string;
        result = [];
        includes.do { |include|
            if (includes.includes(include)) {
                if (include.isString) {
                    string = "\\include \"%\"".format(include);
                    result = result.add(string);
                } {
                    result = result.add(include.format);
                };
            };
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedSchemeSettings

    a = FoscLilypondFile(defaultPaperSize: #['a5', 'portrait'], globalStaffSize: 16);
    a.defaultPaperSize;
    a.prGetFormattedSchemeSettings;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedSchemeSettings {
        var result, dimension, orientation, string;
        result = [];
        if (defaultPaperSize.notNil) {
            # dimension, orientation = defaultPaperSize;
            string = "#(set-default-paper-size \"%\" '%)";
            string = string.format(dimension, orientation);
            result = result.add(string);
        };
        if (globalStaffSize.notNil) {
            string = "#(set-global-staff-size %)";
            string = string.format(globalStaffSize);
            result = result.add(string);
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.prGetFormatPieces.join("\n\n");
    }
}
