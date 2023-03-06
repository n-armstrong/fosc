/* ------------------------------------------------------------------------------------------------------------
• FoscTupletSpecifier

Tuplet specifier.
------------------------------------------------------------------------------------------------------------ */
FoscTupletSpecifier : Fosc {
    var <denominator, <isDiminution, <durationBracket, <extractTrivial, <forceFraction, <rewriteDots;
    var <rewriteRestFilled, <rewriteSustained, <trivialize;
    *new { |denominator, isDiminution, durationBracket=false, extractTrivial=false, forceFraction=false,
        rewriteDots=false, rewriteRestFilled=false, rewriteSustained=false, trivialize=false|    
        ^super.new.init(denominator, isDiminution, durationBracket, extractTrivial, forceFraction, rewriteDots,
            rewriteRestFilled, rewriteSustained, trivialize);
    }
    init { |argDenominator, argIsDiminution, argDurationBracket, argExtractTrivial, argForceFraction,
        argRewriteDots, argRewriteRestFilled, argRewriteSustained, argTrivialize|
        
        if (argDenominator.notNil) { denominator = argDenominator };
        isDiminution = argIsDiminution;
        durationBracket = argDurationBracket;
        extractTrivial = argExtractTrivial;
        forceFraction = argForceFraction;
        rewriteDots = argRewriteDots;
        rewriteRestFilled = argRewriteRestFilled;
        rewriteSustained = argRewriteSustained;
        trivialize = argTrivialize;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |selections, divisions|
        this.prApplyDenominator(selections, divisions);
        this.prForceFraction(selections);
        this.prTrivialize(selections);
        this.prRewriteDots(selections);
        selections = this.prRewriteSustained(selections);
        selections = this.prRewriteRestFilled(selections);
        selections = this.prExtractTrivial(selections);
        this.prToggleProlation(selections);
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prApplyDenominator

    m = FoscTupletMaker().([1/4], #[[2,1],[1],[1,3]]); 
    m = FoscTupletSpecifier(denominator: 4).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prApplyDenominator { |selections, divisions|
        var tuplets, localDenominator, division, unitDuration, duration, denominator_, nonreducedFraction;
        
        if (denominator.isNil) { ^this };
        tuplets = all(FoscIteration(selections).components(type: FoscTuplet));
        if (divisions.isNil) { divisions = Array.newClear(tuplets.size) };
        assert(selections.size == divisions.size);
        assert(tuplets.size == divisions.size);
        localDenominator = denominator;
        if (localDenominator.isSequenceableCollection) {
            localDenominator = FoscDuration(localDenominator);
        };
        tuplets.do { |tuplet, i|
            division = divisions[i];
            case
            { localDenominator == 'divisions' } {
                tuplet.denominator_(division.numerator);
            }
            { localDenominator.isKindOf(FoscDuration) } {
                unitDuration = localDenominator;
                assert(unitDuration.numerator == 1);
                duration = tuplet.prGetDuration;
                denominator_ = unitDuration.denominator;
                nonreducedFraction = duration.withDenominator(denominator_);
                tuplet.denominator_(nonreducedFraction.numerator);
            }
            { localDenominator > 0 && { localDenominator.isInteger } } {
                tuplet.denominator_(localDenominator);
            }
            {
                ^throw("%:%: invalid value for denominator: %"
                    .format(this.species, thisMethod.name, localDenominator));
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prExtractTrivial
    
    m = FoscTupletMaker().([1/4], #[[2,1],[1],[1,3]]); 
    m = FoscTupletSpecifier(extractTrivial: true).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prExtractTrivial { |selections|
        var newSelections, newSelection, tuplet, contents;

        if (extractTrivial.not) { ^selections };
        newSelections = [];
    
        selections.do { |selection|
            newSelection = [];
            selection.do { |component|
                if (component.isKindOf(FoscTuplet) && { component.isTrivial }) {
                    tuplet = component;
                    contents = mutate(tuplet).ejectContents;
                    assert(contents.isKindOf(FoscSelection));
                    newSelection = newSelection.addAll(contents.items);
                } {
                    newSelection = newSelection.add(component); 
                };  
            };
            newSelection = FoscSelection(newSelection);
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prForceFraction

    m = FoscTupletMaker().([1/4], #[[2,1],[1],[1,3]]); 
    m = FoscTupletSpecifier(forceFraction: true).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prForceFraction { |selections|
        if (forceFraction.not) { ^this };
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            tuplet.forceFraction_(true);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteDots

    m = FoscTupletMaker().([1/4], #[[2,1],[1],[1,3]]); 
    m = FoscTupletSpecifier(rewriteDots: true).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prRewriteDots { |selections|
        if (rewriteDots.not) { ^this };
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            tuplet.rewriteDots;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteRestFilled


    • rewrite rest-filled

    m = FoscTupletMaker().([1/4], #[[-2,-1],[1],[-1,-3]]);
    // FoscLilyPondFile.rhythm(m).show;
    m = FoscTupletSpecifier(rewriteRestFilled: true).(m);
    FoscLilyPondFile.rhythm(m).show;


    • rewrite rest-filled and extract trivial

    m = FoscTupletMaker().([1/4], #[[-2,-1],[1],[-1,-4]]);
    m = FoscTupletSpecifier(extractTrivial: true, rewriteRestFilled: true).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prRewriteRestFilled { |selections|
        var newSelections, newSelection, maker, duration, rests;

        if (rewriteRestFilled.not) { ^selections };
        newSelections = [];
        maker = FoscLeafMaker();
    
        selections.do { |selection|
            newSelection = [];
            selection.do { |component|
                if (FoscTupletSpecifier.prIsRestFilledTuplet(component)) {
                    duration = component.prGetDuration;
                    rests = maker.([nil], [duration]);
                    mutate(component.selectLeaves).replace(rests);
                    component.multiplier_(FoscMultiplier(1));
                    newSelection = newSelection.add(component);
                } {
                    newSelection = newSelection.add(component); 
                };  
            };
            newSelection = FoscSelection(newSelection);
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteSustained

    
    • rewrite sustained

    m = FoscTupletMaker().([1/4], #[[4,1],[4,1],[4,1],[4,1]]);
    FoscSelection(m).leaves[1..6].tie;
    // FoscLilyPondFile.rhythm(m).show;
    m = FoscTupletSpecifier(rewriteSustained: true).(m);
    FoscLilyPondFile.rhythm(m).show;


    • rewrite sustained and extract trivial

    m = FoscTupletMaker().([1/4], #[[4,1],[4,1],[4,1],[4,1]]);
    FoscSelection(m).leaves[1..7].tie;
    m = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prRewriteSustained { |selections|
        var newSelections, newSelection, tuplet, duration, leaves, hasTie;

        if (rewriteSustained.not) { ^selections };
        newSelections = [];
    
        selections.do { |selection|
            newSelection = [];
            selection.do { |component|
                if (FoscTupletSpecifier.prIsSustainedTuplet(component)) {
                    tuplet = component;
                    duration = tuplet.prGetDuration;
                    leaves = tuplet.selectLeaves;
                    hasTie = leaves.last.prHasIndicator(FoscTie);
                    leaves[1..].do { |leaf| tuplet.remove(leaf) };
                    assert(tuplet.size == 1);
                    if (hasTie.not) { tuplet[0].detach(FoscTie) };
                    tuplet[0].prSetDuration(duration);
                    tuplet.multiplier_(FoscMultiplier(1));
                    newSelection = newSelection.add(tuplet);
                } {
                    newSelection = newSelection.add(component); 
                };  
            };
            newSelection = FoscSelection(newSelection);
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prToggleProlation

    m = FoscTupletMaker().([1/4], #[[2,1],[4,1],[2,3]]);
    m = FoscTupletSpecifier(isDiminution: false).(m);
    FoscLilyPondFile.rhythm(m).show;
    -------------------------------------------------------------------------------------------------------- */
    prToggleProlation { |selections|
        if (isDiminution.isNil) { ^this };
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            if (
                (isDiminution && tuplet.isDiminution.not)
                || { isDiminution.not && tuplet.isAugmentation.not }
            ) {
                tuplet.toggleProlation;
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prTrivialize
    -------------------------------------------------------------------------------------------------------- */
    prTrivialize { |selections|
        if (trivialize.not) { ^this };
        FoscIteration(selections).components(type: FoscTuplet).do { |tuplet|
            tuplet.trivialize;
        }; 
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prIsRestFilledTuplet

    Is true when all leaves in tuplet are rests.

    a = FoscTuplet(2/3, [FoscRest(2/4), FoscRest(1/4)]);
    FoscTupletSpecifier.prIsRestFilledTuplet(a);

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscRest(1/4)]);
    FoscTupletSpecifier.prIsRestFilledTuplet(a);
    -------------------------------------------------------------------------------------------------------- */
    *prIsRestFilledTuplet { |tuplet|
        if (tuplet.isKindOf(FoscTuplet).not) { ^false };
        ^tuplet.selectLeaves.every { |leaf| leaf.isKindOf(FoscRest) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prIsSustainedTuplet

    Is true when 'argument' is sustained tuplet.

    a = FoscTuplet(2/3, [FoscNote(60, 3/4)]);
    FoscTupletSpecifier.prIsSustainedTuplet(a);

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscNote(60, 1/4)]);
    FoscTupletSpecifier.prIsSustainedTuplet(a);
    -------------------------------------------------------------------------------------------------------- */
    *prIsSustainedTuplet { |object|
        var logicalTieHeadCount=0, leaves, logicalTie;
        if (object.isKindOf(FoscTuplet).not) { ^false };
        leaves = FoscSelection(object).leaves;   
        leaves.do { |leaf|
            logicalTie = leaf.prGetLogicalTie;
            if (logicalTie.head === leaf) {
                logicalTieHeadCount = logicalTieHeadCount + 1;
            };
        };
        if (logicalTieHeadCount == 0) { ^true };
        logicalTie = leaves[0].prGetLogicalTie;
        if (logicalTie.head === leaves[0] && { logicalTieHeadCount == 1 }) { ^true };
        ^false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • denominator
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • diminution
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • durationBracket
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • extractTrivial
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • forceFraction
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteDots
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteSustained
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteRestFilled
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • trivialize
    -------------------------------------------------------------------------------------------------------- */
}
