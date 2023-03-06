/* ------------------------------------------------------------------------------------------------------------
• FoscRhythmMaker


!!!TODO:
- For consistency with FoscLeafMaker, return a selection rather than an Array of selections
- Add a 'pitches' argument to value method
- replace specifiers with FoscCommands (see Abjad:Command subclasses)



Object model of a partially evaluated function that accepts a (possibly empty) list of durations as input and returns a list of selections as output. Output structured one selection per division with each selection wrapping a single fixed-duration tuplet.

Usage follows the two-step configure-once / call-repeatedly pattern shown here.


• Example 1

a = FoscRhythmMaker();
a.(durations: [1/4], divisions: #[[1,1],[3,2],[4,3]]);
a.show;


• Example 2

a = FoscRhythmMaker();
a.(durations: [2/16, 3/16, 5/32], divisions: #[[1,1,1,1]]);
a.show;


• Example 3

Negative values in 'divisions' specify rests.

a = FoscRhythmMaker();
a.(durations: [1/4], divisions: #[[-2,1],[3,2],[4,-3]]);
a.show;


• Example 4

Apply a mask to rhythms.

a = FoscRhythmMaker();
a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[6,4,3,7]);
a.show;


• Example 5

Mask patterns repeat cyclically.

a = FoscRhythmMaker();
a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[1,3]);
a.show;


• Example 6

Negative values in a mask specify rests.

a = FoscRhythmMaker();
a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[-6,4,3,-7]);
a.show;


• Example 7

!!!TODO: returns a beam error on lilypond compilation

Override the defaults.

a = FoscRhythmMaker(beamRests: true);
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,-1,4,2,3,-7]);
b.show;
------------------------------------------------------------------------------------------------------------ */
FoscRhythmMaker : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <beamEachDuration, <beamRests, <extractTrivialTuplets, <rewriteRestFilledTuplets, <rewriteSustainedTuplets;
    var beamSpecifier, durationSpecifier, meterSpecifier, tupletSpecifier, selections;
    *new { |beamEachDuration=true, beamRests=false, extractTrivialTuplets=true, rewriteRestFilledTuplets=true, rewriteSustainedTuplets=true|

        ^super.new.init(beamEachDuration, beamRests, extractTrivialTuplets, rewriteRestFilledTuplets, rewriteSustainedTuplets);
    }
    init { |argBeamEachDuration, argBeamRests, argExtractTrivialTuplets, argRewriteRestFilledTuplets, argRewriteSustainedTuplets|
        
        beamEachDuration = argBeamEachDuration;
        beamRests = argBeamRests;
        extractTrivialTuplets = argExtractTrivialTuplets;
        rewriteRestFilledTuplets = argRewriteRestFilledTuplets;
        rewriteSustainedTuplets = argRewriteSustainedTuplets;


        beamSpecifier = FoscBeamSpecifier(
            beamRests: beamRests,
            beamEachDivision: beamEachDuration
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
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • defaultStylesheetPath
    -------------------------------------------------------------------------------------------------------- */
    defaultStylesheetPath {
        ^"%/rhythm-sketch.ily".format(Fosc.stylesheetDirectory);
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    a = FoscRhythmMaker();
    a.(durations: [1/4], divisions: #[[1,1],[3,2],[4,3]]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes|
        var template, score, includesPath, lilypondFile;

        template = FoscGroupedRhythmicStavesScoreTemplate(staffCount: 1);
        score = template.value;
        score['v1'].add(selections.deepCopy);

        if (includes.notNil) {
            if (includes.isSequenceableCollection.not) { includes = [includes] };
            includes = includes ++ [this.defaultStylesheetPath];
        } {
            includes = [this.defaultStylesheetPath];
        };

        lilypondFile = score.illustrate(paperSize, staffSize, includes);
        
        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • show
    -------------------------------------------------------------------------------------------------------- */
    show { |paperSize, staffSize=16, includes|
        ^this.illustrate(paperSize, staffSize, includes).show;
    }
    /* --------------------------------------------------------------------------------------------------------
    • value


    a = FoscRhythmMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[-6,4,3,-7]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    value { |durations, divisions, mask|
        selections = this.prMakeMusic(durations, divisions);
        selections = this.prApplyMask(selections, mask);

        if (tupletSpecifier.notNil) { selections = tupletSpecifier.(selections, durations) };
        if (meterSpecifier.notNil) { selections = meterSpecifier.(selections, durations) };
        //!!!TODO: if (durationSpecifier.notNil) { selections = durationSpecifier.(selections, durations) };
        
        this.prValidateSelections(selections);
        this.prValidateTuplets(selections);

        if (beamSpecifier.notNil) { beamSpecifier.(selections) };
        
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • beamEachDuration
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • beamRests
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • extractTrivial
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteRestFilled
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteSustained
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
    // prApplyMask { |selections, mask|
    //     var newSelections, containers, container, logicalTies, leaves, newSelection;

    //     if (mask.isNil) { ^selections };

    //     newSelections = [];
    //     containers = [];

    //     selections.do { |selection|
    //         container = FoscContainer();
    //         container.addAll(selection);
    //         containers = containers.add(container);
    //     };

    //     logicalTies = FoscSelection(selections).selectLogicalTies;
    //     mask = mask.repeatToAbsSum(logicalTies.size); // mask pattern repeats cyclically
        
    //     logicalTies.partitionBySizes(mask.abs).do { |each, i|
    //         leaves = each.selectLeaves;
    //         if (mask[i] > 0) { leaves.prFuseLeaves } { leaves.prFuseLeavesAndReplaceWithRests };
    //     };

    //     containers.do { |container|
    //         newSelection = container.prEjectContents;
    //         newSelections = newSelections.add(newSelection);
    //     };

    //     ^newSelections;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prCoerceDivisions
    -------------------------------------------------------------------------------------------------------- */
    prCoerceDivisions { |durations|
        if (durations.isSequenceableCollection.not) { durations = [durations] };
        durations = durations.collect { |each| FoscNonreducedFraction(each) };
        ^durations;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMakeMusic

    a = FoscRhythmMaker();
    a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[-6,4,3,-7]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prMakeMusic { |durations, divisions|
        var n, ratio, duration, selection;
        
        n = [durations.size, divisions.size].maxItem;
        durations = durations.wrapExtend(n);
        divisions = divisions.wrapExtend(n);
        selections = [];
        durations = this.prCoerceDivisions(durations);
        
        durations.do { |division, i|
            ratio = divisions[i];
            duration = FoscDuration(division);
            selection = FoscRhythm(duration, ratio).value;
            selections = selections.add(selection);
        };
        
        ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prValidateSelections
    -------------------------------------------------------------------------------------------------------- */
    prValidateSelections { |selections|
        assert(selections.isSequenceableCollection);
        assert(selections.size > 0);
        selections.do { |each|
            if (each.isKindOf(FoscSelection).not) {
                ^throw("%:prValidateSelections: validation failed.".format(this.species));
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prValidateTuplets
    -------------------------------------------------------------------------------------------------------- */
    prValidateTuplets { |selections|
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            if (tuplet.multiplier.isNormalized.not) {
                ^throw("%::prValidateTuplets: tuplet multiplier is not normalized: %."
                    .format(this.species, tuplet.multiplier.str));
            };
            if (tuplet.size < 1) {
                ^throw("%::prValidateTuplets: tuplet has no children.".format(this.species));
            }; 
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prAllAreTupletsOrAllAreLeafSelections
    -------------------------------------------------------------------------------------------------------- */
    *prAllAreTupletsOrAllAreLeafSelections { |expr|
        if (expr.every { |each| each.isKindOf(FoscTuplet) }) { ^true };
        if (expr.every { |each| FoscRhythmMaker.prIsLeafSelection(each) }) { ^true };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prIsLeafSelection
    -------------------------------------------------------------------------------------------------------- */
    *prIsLeafSelection { |expr|
        if (expr.isKindOf(FoscSelection)) { ^expr.every { |each| each.isKindOf(FoscLeaf) } };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prIsSignTuple
    -------------------------------------------------------------------------------------------------------- */
    *prIsSignTuple { |expr|
        var type;
        if (expr.isSequenceableCollection) {
            type = #[-1, 0, 1];
            ^expr.every { |each| type.includes(each) }
        };
        ^false;
    }
}
