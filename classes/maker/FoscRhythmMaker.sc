/* ------------------------------------------------------------------------------------------------------------
• FoscRhythmMaker

Object model of a partially evaluated function that accepts a (possibly empty) list of divisions as input and returns a list of selections as output. Output structured one selection per division with each selection wrapping a single fixed-duration tuplet.

Usage follows the two-step configure-once / call-repeatedly pattern shown here.


• Example 1

a = FoscRhythmMaker();
a.(divisions: [1/4], ratios: #[[1,1],[3,2],[4,3]]);
a.show;


• Example 2

a = FoscRhythmMaker();
a.(divisions: [2/16, 3/16, 5/32], ratios: #[[2,1],[3,2],[4,3]]);
a.show;


• Example 3 !!!TODO: DEPRECATE THIS BEHAVIOUR ??

Floating point-values specify the beginnings of ties.

a = FoscRhythmMaker();
a.(divisions: [2/16, 3/16, 5/32], ratios: #[[2,1.0],[3,2.0],[4,3]]);
a.show;


• Example 4

Negative values in tuplet ratio specify rests.

a = FoscRhythmMaker();
a.(divisions: [2/16, 3/16, 5/32], ratios: #[[-2,1],[3,2],[4,-3]]);
a.show;


• Example 5

Patterns may be used as arguments.

a = FoscRhythmMaker();
a.(divisions: [1/8], ratios: Pseq(#[[-2,1],[3,2]], 7));
a.show;


• Example 6

Patterns may be used as arguments.

a = FoscRhythmMaker();
a.(divisions: Pseq([[1,8],[3,16]], 7), ratios: #[[-2,3]]);
a.show;


• Example 7

Patterns may be used as arguments.

a = FoscRhythmMaker();
a.(divisions: Pseq([[1,8],[3,16]], 7), ratios: Pseq(#[[-2,3], [3, -2]], 4));
a.show;


• Example 8

Apply sustain mask to tuplets.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p, hold: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;

a = FoscRhythmMaker();
m = FoscSustainMask(FoscPattern.sizes(#[4,-3,5,-4,4]));
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: m);
a.show;


• Example 9

With tuplet specifier and beam specifier.

t = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
b = FoscBeamSpecifier(beamRests: false);
a = FoscRhythmMaker(beamSpecifier: b, tupletSpecifier: t);
m = FoscSustainMask(FoscPattern.sizes(#[4,-3,5,-4,4]));
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], mask: m);
a.show;


• Example 10

Beam rests and include stemlets.

t = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
b = FoscBeamSpecifier(beamRests: true, stemletLength: 2);
a = FoscRhythmMaker(beamSpecifier: b, tupletSpecifier: t);
m = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], mask: FoscFuseMask(#[4,-3,5,-8]));
a.show;


• Example 11

Extract trivial tuplets, rewrite sustained tuplets, and rewrite rest-filled tuplets.

t = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
a = FoscRhythmMaker(tupletSpecifier: t);
m = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], mask: FoscFuseMask(#[4,-3,5,-8]));
m.selectRuns.do { |run| run.beam };
a.show;


• Example 12

!!!TODO: BROKEN: tuplet specifier causes entire final tuplet selection to be extracted

Bypass specifiers in factory stage. Apply them after further transformations on selections.

a = FoscRhythmMaker();
m = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], mask: FoscFuseMask(#[4,-3,5,-8]));
m = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true).(m);
m.selectRuns.do { |run| run.beam };
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscRhythmMaker : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <beamSpecifier, <durationSpecifier, <meterSpecifier, <tupletSpecifier;
    var selections, previousState;
    *new { |beamSpecifier, durationSpecifier, meterSpecifier, tupletSpecifier|  

        if (beamSpecifier.notNil) {
            assert(beamSpecifier.isKindOf(FoscBeamSpecifier), thisMethod, 'beamSpecifier', beamSpecifier);
        };

        if (durationSpecifier.notNil) {
            assert(durationSpecifier.isKindOf(FoscDurationSpecifier), thisMethod, 'durationSpecifier',
                durationSpecifier);
        };
        
        if (meterSpecifier.notNil) {
            assert(meterSpecifier.isKindOf(FoscMeterSpecifier), thisMethod, 'meterSpecifier', meterSpecifier);
        };
        
        if (tupletSpecifier.notNil) {
            assert(tupletSpecifier.isKindOf(FoscTupletSpecifier), thisMethod, 'tupletSpecifier',
                tupletSpecifier);
        };

        ^super.new.init(beamSpecifier, durationSpecifier, meterSpecifier, tupletSpecifier);
    }
    init { |argBeamSpecifier, argDurationSpecifier, argMeterSpecifier, argTupletSpecifier|
        beamSpecifier = argBeamSpecifier;
        durationSpecifier = argDurationSpecifier;
        meterSpecifier = argMeterSpecifier;
        tupletSpecifier = argTupletSpecifier;
        previousState = (
            'divisionsConsumed': 0,
            'incompleteLastNote': false,
            'logicalTiesProduced': 0  
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates rhythm-maker.
    
    Returns LilyPond file.
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |timeSignatures|
        if (selections.isNil) { throw("%:illustrate: no music to show.".format(this.species)) };
        ^FoscLilypondFile.rhythm(selections, timeSignatures);
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    a = FoscRhythmMaker();
    a.(divisions: [1/4], ratios: #[1,1,1,1,1] ! 4);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    show { |timeSignatures|
        var lilypondFile;
        lilypondFile = this.illustrate(timeSignatures);
        lilypondFile.show;
    }
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |divisions, ratios, masks|
        selections = this.prMakeMusic(divisions, ratios);
        selections = this.prApplyLogicalTieMasks(selections, masks);
        selections = this.prApplySpecifiers(selections, divisions);
        this.prGetBeamSpecifier.(selections);
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • beamSpecifier

    Gets beam specifier.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • divisionMasks

    Gets division masks.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • durationSpecifier

    Gets duration spelling specifier.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • logicalTieMasks

    Gets logical tie masks.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tieSpecifier

    Gets tie specifier.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tupletSpecifier

    Gets tuplet spelling specifier.
    -------------------------------------------------------------------------------------------------------- */
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
        var prototype;
        if (expr.isSequenceableCollection) {
            prototype = [-1, 0, 1];
            ^expr.every { |each| prototype.includes(each) }
        };
        ^false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prApplyLogicalTieMasks


    • Example 1

    p = #[-1,3,3,3,3,3,3,-1]; // sustain ratios
    m = FoscSustainMask(FoscPattern(p.abs.offsets.drop(-1)), hold: true);
    n = FoscSilenceMask(FoscPattern.indices(p.indicesForWhich { |item| item < 0 }));
    t = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
    a = FoscRhythmMaker(tupletSpecifier: t);
    a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m, n]);
    a.show;


    • Example 2

    p = #[-1,1,2,-1,1,1,-1,1,-1,1,1,-1,-1,3,-3];
    m = FoscSustainMask(FoscPattern(p.abs.offsets.drop(-1)), hold: true);
    n = FoscSilenceMask(FoscPattern.indices(p.indicesForWhich { |item| item < 0 }));
    t = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
    a = FoscRhythmMaker(tupletSpecifier: t);
    a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m, n]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prApplyLogicalTieMasks { |selections, masks|
        var container;
        if (masks.isNil) { ^selections };
        if (masks.isSequenceableCollection.not) { masks = [masks] };
        container = FoscContainer(selections);
        masks.do { |mask| selections = mask.(FoscSelection(container)) };
        selections = container.prEjectContents;
        selections = selections.items.collect { |each| FoscSelection(each) };
        ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prPrepareMasks

    x = FoscSilenceMask(FoscPattern(#[0,1]));
    y = FoscSilenceMask(FoscPattern(#[0,4,5]));
    a = FoscRhythmMaker().(1/4 ! 4, #[[1,1,1,1,1]], masks: [x,y]);

    p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(7);
    m = FoscSustainMask(p);
    a = FoscRhythmMaker().(1/4 ! 4, #[[1,1,1,1,1]], masks: [m]);
    -------------------------------------------------------------------------------------------------------- */
    prPrepareMasks { |masks|
        var prototype;
        prototype = [FoscSilenceMask, FoscSustainMask];
        if (masks.isNil) { ^nil };
        if (masks.isKindOf(FoscPattern)) { masks = [masks] };
        if (prototype.any { |type| masks.isKindOf(type) }) { masks = [masks] };
        masks = FoscPatternList(masks);
        ^masks;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prPreviousDivisionsConsumed
    
    def _previous_divisions_consumed(self):
        if not self.previous_state:
            return 0
        return self.previous_state.get('divisions_consumed', 0)
    -------------------------------------------------------------------------------------------------------- */
    prPreviousDivisionsConsumed {
        ^previousState['divisionsConsumed'];  
    }
    /* --------------------------------------------------------------------------------------------------------
    • prPreviousIncompleteLastNote
    
    def _previous_incomplete_last_note(self):
        if not self.previous_state:
            return False
        return self.previous_state.get('incomplete_last_note', False)
    -------------------------------------------------------------------------------------------------------- */
    // prPreviousIncompleteLastNote {
    //     ^previousState['incompleteLastNote'];  
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prPreviousLogicalTiesProduced
    
    def _previous_logical_ties_produced(self):
        if not self.previous_state:
            return 0
        return self.previous_state.get('logical_ties_produced', 0)
    -------------------------------------------------------------------------------------------------------- */
    // prPreviousLogicalTiesProduced {
    //     ^previousState['logicalTiesProduced'];
    // } 
    /* --------------------------------------------------------------------------------------------------------
    • prApplyMeterSpecifier
    -------------------------------------------------------------------------------------------------------- */
    prApplyMeterSpecifier { |selections|
        if (meterSpecifier.isNil) { ^selections };
        selections = meterSpecifier.(selections);
        ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prApplySpecifiers
    -------------------------------------------------------------------------------------------------------- */
    prApplySpecifiers { |selections, divisions|
        selections = this.prApplyTupletSpecifier(selections, divisions);
        selections = this.prApplyMeterSpecifier(selections);
        //!!!TODO: this.prApplyTieSpecifier(selections);
        //!!!TODO: selections = this.prApplyLogicalTieMasks(selections);
        this.prValidateSelections(selections);
        this.prValidateTuplets(selections);
        ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prApplyTieSpecifier
    -------------------------------------------------------------------------------------------------------- */
    // prApplyPhraseSpecifier { |selections|
    //     var specifier;
    //     specifier = this.prGetPhraseSpecifier;
    //     ^specifier.(selections);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prApplyTupletSpecifier
    -------------------------------------------------------------------------------------------------------- */
    prApplyTupletSpecifier { |selections, divisions|
        if (tupletSpecifier.isNil) { ^selections };
        selections = tupletSpecifier.(selections, divisions);
        ^selections;
        // var specifier;
        // specifier = this.prGetTupletSpecifier;
        // selections = specifier.(selections, divisions);
        // ^selections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCoerceDivisions
    -------------------------------------------------------------------------------------------------------- */
    prCoerceDivisions { |divisions|
        if (divisions.isSequenceableCollection.not) { divisions = [divisions] };
        divisions = divisions.collect { |each| FoscNonreducedFraction(each) };
        ^divisions;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetBeamSpecifier
    -------------------------------------------------------------------------------------------------------- */
    prGetBeamSpecifier {
        if (beamSpecifier.notNil) { ^beamSpecifier };
        ^FoscBeamSpecifier(beamEachDivision: true);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDurationSpecifier
    -------------------------------------------------------------------------------------------------------- */
    prGetDurationSpecifier {
        if (durationSpecifier.notNil) { ^durationSpecifier };
        ^FoscDurationSpecifier();
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTupletSpecifier
    -------------------------------------------------------------------------------------------------------- */
    prGetTupletSpecifier {
        if (tupletSpecifier.notNil) { ^tupletSpecifier };
        ^FoscTupletSpecifier();
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMakeMusic
    -------------------------------------------------------------------------------------------------------- */
    prMakeMusic { |divisions, ratios|
        var n, ratio, duration, selection;
        
        case
        { divisions.isSequenceableCollection && ratios.isSequenceableCollection } {
            n = [divisions.size, ratios.size].maxItem;
            divisions = divisions.wrapExtend(n);
            ratios = ratios.wrapExtend(n);
        }
        { divisions.isSequenceableCollection && ratios.isKindOf(Pattern) } {
            if (ratios.respondsTo('repeats') && { ratios.repeats != inf }) {
                n = ratios.repeats;
            } {
                n = divisions.size;
            };
            divisions = divisions.wrapExtend(n);
            ratios = ratios.asStream.nextN(n);
        }
        { divisions.isKindOf(Pattern) && ratios.isSequenceableCollection } {
            if (divisions.respondsTo('repeats') && { divisions.repeats != inf }) {
                n = divisions.repeats;
            } {
                n = ratios.size;
            };
            divisions = divisions.asStream.nextN(n);
            ratios = ratios.wrapExtend(n);
        }
        { divisions.isKindOf(Pattern) && ratios.isKindOf(Pattern) } {
            case 
            { divisions.respondsTo('repeats') && ratios.respondsTo('repeats') } {
                n = [divisions.repeats, ratios.repeats].minItem;
            }
            { divisions.respondsTo('repeats') && (ratios.respondsTo('repeats').not) } {
                n = divisions.repeats;
            }
            { (divisions.respondsTo('repeats').not) && ratios.respondsTo('repeats') } {
                n = ratios.repeats;
            };
            divisions = divisions.asStream.nextN(n);
            ratios = ratios.asStream.nextN(n);
        };
        

        selections = [];
        divisions = this.prCoerceDivisions(divisions);
        assert(divisions.every { |each| each.isKindOf(FoscNonreducedFraction) });
        
        divisions.do { |division, i|
            ratio = ratios[i];
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
                throw("%:prValidateSelections: validation failed.".format(this.species));
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prValidateTuplets
    -------------------------------------------------------------------------------------------------------- */
    prValidateTuplets { |selections|
        FoscIteration(selections).components(prototype: FoscTuplet).do { |tuplet|
            if (tuplet.multiplier.isNormalized.not) {
                throw("%::prValidateTuplets: tuplet multiplier is not normalized: %."
                    .format(this.species, tuplet.multiplier.str));
            };
            if (tuplet.size < 1) {
                throw("%::prValidateTuplets: tuplet has no children.".format(this.species));
            }; 
        };
    }
}
