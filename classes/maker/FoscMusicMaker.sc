/* ------------------------------------------------------------------------------------------------------------
• FoscMusicMaker


!!!TODO:
- replace specifiers with FoscCommands (see Abjad:Command subclasses)
- only permit durations with a power of 2 denominator ?
- do not permit negative values in durations ? (how to replicate 'nil' notes in FoscLeafMaker ?)
   - perhaps this can only be done with a mask ?


Usage follows the two-step configure-once / call-repeatedly pattern shown here.


• Example 1

Make music with numbered pitches. 'durations' wrap extends to length of 'pitches' when 'divisions' is nil.

a = FoscMusicMaker();
b = a.(durations: [1/4], pitches: #[60,62,64,65]);
b.show;


• Example 2

Make music with a string of lilypond pitch names

a = FoscMusicMaker();
b = a.(durations: [1/4], pitches: "c' d' e' f'");
b.show;


• Example 3

Chords can be freely combined with single pitches.

a = FoscMusicMaker();
b = a.(durations: [1/4], pitches: "c' d' ef' <c' e' g'>");
b.show;


• Example 4

Chords can be freely combined with single pitches.

a = FoscMusicMaker();
b = a.(durations: [1/4], pitches: #[60,62,63,[60,64,67]]);
b.show;


• Example 5

'divisions' embed into 'durations'

a = FoscMusicMaker();
b = a.(durations: [1/4], divisions: #[[1,1],[3,2],[4,3]]);
b.show;


• Example 6

Negative values in 'divisions' specify rests.

a = FoscMusicMaker();
b = a.(durations: [1/4, 1/8, 3/8], divisions: #[[-1,1],[3,2],[4,-3]]);
b.show;


• Example 7

Divisions can be nested in rhythm-tree syntax

a = FoscMusicMaker();
b = a.(durations: [1/4], divisions: #[[-1,1],[3,[2,[1,1,1]]],[4,-3]]);
b.show;


• Example 8

Apply a mask. Mask patterns repeat cyclically. Negative values specify rests.


a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1]], pitches: #[60,62]);
b.show;

a = FoscMusicMaker(beamEachRun: true);
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,1,-1], pitches: #[60,62]);
b.show;


a = FoscMusicMaker(beamEachRun: false);
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1]], mask: #[2,1], pitches: #[60,62]);
b.show;


• Example 9

Pitches are applied after the mask.

a = FoscMusicMaker(beamEachRun: true);
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,1,-1], pitches: "c' d' ef' f'");
b.show;


• Example 10

Use FoscMask to specify a masking 'pattern' that repeats every division in 'divisions'

a = FoscMusicMaker();
m = FoscMask(divisions: #[6,5,4], pattern: #[[1,1,-inf]]);
b = a.(durations: 1/8 ! 3, divisions: #[[1,1,1,1,1]], mask: m);
b.show;
------------------------------------------------------------------------------------------------------------ */
FoscMusicMaker : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <beamEachDivision, <beamEachRun, <beamRests, <extractTrivialTuplets, <rewriteRestFilledTuplets, <rewriteSustainedTuplets;
    var beamSpecifier, durationSpecifier, meterSpecifier, tupletSpecifier;
    *new { |beamEachDivision=true, beamEachRun=false, beamRests=false, extractTrivialTuplets=true, rewriteRestFilledTuplets=true, rewriteSustainedTuplets=true|

        ^super.new.init(beamEachDivision, beamEachRun, beamRests, extractTrivialTuplets, rewriteRestFilledTuplets, rewriteSustainedTuplets);
    }
    init { |argBeamEachDivision, argBeamEachRun, argBeamRests, argExtractTrivialTuplets, argRewriteRestFilledTuplets, argRewriteSustainedTuplets|
        
        beamEachDivision = argBeamEachDivision;
        beamEachRun = argBeamEachRun;
        beamRests = argBeamRests;
        extractTrivialTuplets = argExtractTrivialTuplets;
        rewriteRestFilledTuplets = argRewriteRestFilledTuplets;
        rewriteSustainedTuplets = argRewriteSustainedTuplets;

        if (beamEachRun) { beamEachDivision = false };
        if (beamEachDivision) { beamEachRun = false };

        beamSpecifier = FoscBeamSpecifier(
            beamEachDivision: beamEachDivision,
            beamEachRun: beamEachRun,
            beamRests: beamRests
        );
        
        //durationSpecifier = argDurationSpecifier;
        
        //meterSpecifier = argMeterSpecifier;
        
        tupletSpecifier = FoscTupletSpecifier(
            extractTrivial: extractTrivialTuplets,
            rewriteRestFilled: rewriteRestFilledTuplets,
            rewriteSustained: rewriteSustainedTuplets
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value

    a = FoscMusicMaker();
    b = a.(durations: [1/8], divisions: #[2,3]);
    b = a.(durations: [1/8], mask: #[-1,1,1,1,-1,1,1,1], pitches: (60..65));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: [1/8], divisions: #[2,3], mask: #[-1,1,1,1], pitches: (60..67));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 8, divisions: #[2,3,2], pitches: (60..66));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: [[5,16]], mask: #[-1,1,1,1,1], pitches: (60..64));
    b.show;


    a = FoscMusicMaker();
    b = a.(durations: 1/8 ! 4, divisions: #[[1,1,1,1,1]], mask: #[1,-10,1,-8]);
    b.show;


    a = FoscMusicMaker();
    m = FoscMask(divisions: #[6,5,4], pattern: #[[1,1,-inf]]);
    b = a.(durations: 1/8 ! 3, divisions: #[[1,1,1,1,1]], mask: m);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    value { |durations, divisions, mask, pitches|
        var selections, selection;

        if (pitches.isString) { pitches = FoscPitchManager.pitchStringToPitches(pitches) };
        if (mask.isKindOf(FoscMask)) { mask = mask.mask };
        selections = this.prMakeSelections(durations, divisions, pitches);
        selections = this.prApplyMask(selections, mask);
        if (tupletSpecifier.notNil) { selections = tupletSpecifier.(selections, durations) };
        if (meterSpecifier.notNil) { selections = meterSpecifier.(selections, durations) };
        // if (durationSpecifier.notNil) { selections = durationSpecifier.(selections, durations) };
        this.prValidate(selections);
        if (beamSpecifier.notNil) { beamSpecifier.(selections) };
        selection = FoscSelection(selections).flat;
        if (pitches.notNil) { mutate(selection).rewritePitches(pitches, isCyclic: true) };

        ^selection;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • beamEachDivision
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • beamEachRun
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • beamRests
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • extractTrivialTuplets
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • respellAccidentals
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteMeter
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteRestFilledTuplets
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteSustainedTuplets
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prApplyMask
    -------------------------------------------------------------------------------------------------------- */
    prApplyMask { |selections, mask|
        var newSelections, containers, container, logicalTies, leaves, newSelection;

        if (mask.isNil) { ^selections };

        newSelections = [];
        containers = [];

        selections.do { |selection|
            container = FoscContainer();
            container.addAll(selection);
            containers = containers.add(container);
        };

        FoscSelection(selections).prApplyMask(mask, isCyclic: true);

        containers.do { |container|
            newSelection = container.prEjectContents;
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMakeSelections

    a = FoscMusicMaker();
    b = a.(durations: 1/8 ! 8, mask: #[-1,1,1,1,-1,1,1,1], pitches: (60..65));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: [1/8], divisions: #[2,3], mask: #[-1,1,1,1], pitches: (60..67));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: [1/4], divisions: #[-2,3,2], pitches: (60..66));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: 5/16 ! 5, mask: #[-1,1,1,1,1], pitches: (60..64));
    b.show;


    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[2,3,2], pitches: (60..66));
    b.show;

    a = FoscMusicMaker();
    b = a.(durations: #[3,1,2,2]/8, pitches: #[60,62]);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    prMakeSelections { |durations, divisions, pitches|
        var size, selections, selection;
        var divSize, durSize;

        if (durations.isNil) { durations = #[0.25] };

        if (pitches.size > durations.size && { divisions.isNil }) {
            divisions = #[[1]];
            size = pitches.size;
        } {
            if (divisions.isNil) { divisions = #[[1]] };
            if (divisions.rank == 1) { divisions = [divisions] };
            size = [durations.size, divisions.size].maxItem;
        };

        divisions = divisions.wrapExtend(size);
        durations = durations.wrapExtend(size);      
        durations = durations.collect { |each| FoscNonreducedFraction(each) };
        selections = [];
        
        durations.do { |duration, i|
            selection = FoscRhythm(duration, divisions[i]).value;
            selections = selections.add(selection);
        };
        
        ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prValidate
    -------------------------------------------------------------------------------------------------------- */
    prValidate { |selections|
        // validate selections
        selections.do { |each|
            if (each.isKindOf(FoscSelection).not) {
                ^throw("%:%: validation failed.".format(this.species, thisMethod.name));
            };
        };

        // validate tuplets
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            if (tuplet.multiplier.isNormalized.not) {
                ^throw("%:%: tuplet multiplier is not normalized: %."
                    .format(this.species, thisMethod.name, tuplet.multiplier.str));
            };
            
            if (tuplet.size < 1) {
                ^throw("%:%: tuplet has no children.".format(this.species, thisMethod.name));
            }; 
        };
    }
}
