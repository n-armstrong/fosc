/* ------------------------------------------------------------------------------------------------------------
• FoscMetronomeMark (abjad 3.0)

Tempo indicator.


• Example 1

Initialize integer-valued metronome mark.

a = FoscScore();
b = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.add(b);
m = FoscMetronomeMark(#[1,4], 90);
b[0].attach(m);
a.show;


• Example 2 !!!TODO: incomplete implementation

Initialize rational-valued metronome mark.

a = FoscScore();
b = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.add(b);
m = FoscMetronomeMark(#[1,4], FoscFraction(182, 2));
b[0].attach(m);
a.show;


• Example 3

Initialize from text, duration, and range.

a = FoscScore();
b = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.add(b);
m = FoscMetronomeMark(#[1,4], #[120, 133], "Quick");
b[0].attach(m);
a.show;


• Example 4 !!!TODO: incomplete implementation

Use rational-value units-per-minute together with custom markup for float-valued metronome marks.

a = FoscScore();
b = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.add(b);
t = FoscMetronomeMark.makeTempoEquationMarkup(#[1,4], 90.1);
m = FoscMetronomeMark(#[1,4], FoscFraction(#[900,10]), customMarkup: t);
b[0].attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscMetronomeMark : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <customMarkup, <hide, <referenceDuration, <textualIndication, <unitsPerMinute;
    var <context='Score', <formatSlot='opening', <mutatesOffsetsInSeconds=true, <parameter='METRONOME_MARK';
    var <persistent=true;
    *new { |referenceDuration, unitsPerMinute, textualIndication, customMarkup, hide=false|
        ^super.new.init(referenceDuration, unitsPerMinute, textualIndication, customMarkup, hide);
    }
    init { |argReferenceDuration, argUnitsPerMinute, argTextualIndication, argCustomMarkup, argHide|
        //!!! INCOMPLETE
        referenceDuration = argReferenceDuration ?? { FoscDuration(1, 4) };
        referenceDuration = FoscDuration(referenceDuration);
        unitsPerMinute = argUnitsPerMinute ? 60;
        textualIndication = argTextualIndication;
        customMarkup = argCustomMarkup;
        hide = argHide;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • + (abjad: __add__)
    -------------------------------------------------------------------------------------------------------- */
    add {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • / (abjad: __div__)
    -------------------------------------------------------------------------------------------------------- */
    div {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • == (not in abjad ?)

    a = FoscMetronomeMark([1, 4], 72);
    b = FoscMetronomeMark([1, 4], 72);
    c = FoscMetronomeMark([1, 8], 72);
    d = FoscMetronomeMark([1, 4], 60);

    a == b;         // true
    a == c;         // false
    a == d;         // false
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        if (expr.isKindOf(this.species).not) { ^false };
        if (referenceDuration != expr.referenceDuration) { ^false };
        if (unitsPerMinute != expr.unitsPerMinute) { ^false };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • < (abjad: __lt__)
    -------------------------------------------------------------------------------------------------------- */
    < {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • * (abjad __mul__)
    -------------------------------------------------------------------------------------------------------- */
    mul {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • (abjad: __rmul__)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • asCompileString (abjad: __str__)
    -------------------------------------------------------------------------------------------------------- */
    str {
        var string;
        string = "% = %".format(referenceDuration.str, unitsPerMinute);
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • - (abjad: __sub__)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • (abjad: __truediv__)
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *makeTempoEquationMarkup
    -------------------------------------------------------------------------------------------------------- */
    *makeTempoEquationMarkup { |referenceDuration, unitsPerMinute|
        var selection, maker, lhsScoreMarkup, equalMarkup, rhsMarkup, markup;
        if (referenceDuration.isKindOf(FoscSelection)) {
            selection = referenceDuration;
        } {
            maker = FoscLeafMaker();
            selection = maker.([60], referenceDuration);
        };
        lhsScoreMarkup = FoscDuration.toScoreMarkup(selection);
        lhsScoreMarkup = lhsScoreMarkup.scale(#[0.75, 0.75]);
        equalMarkup = FoscMarkup("=");
        if (
            unitsPerMinute.isKindOf(FoscFraction)
            && { unitsPerMinute.isIntegerEquivalentNumber.not }
        ) {
            rhsMarkup = FoscMarkup.makeImproperFractionMarkup(unitsPerMinute);
            rhsMarkup = rhsMarkup.raise_(-0.5);
        } {
            rhsMarkup = FoscMarkup(unitsPerMinute);
            rhsMarkup = rhsMarkup.generalAlign('Y', -0.5);
        };  
        markup = lhsScoreMarkup ++ equalMarkup ++ rhsMarkup;
        ^markup;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • durationToSeconds (abjad: duration_to_milliseconds)
    
    

    • Example 1

    A quarter-note lasts a second at quarter equals 60.

    a = FoscMetronomeMark(#[1,4], 60);
    a.durationToSeconds(1/4).asFloat;


    • Example 2

    A quarter-note lasts 2/3 of a second at quarter equals 60.

    a = FoscMetronomeMark(#[1,4], 90);
    a.durationToSeconds(1/4).asFloat;
    -------------------------------------------------------------------------------------------------------- */
    durationToSeconds { |duration|
        var multiplier;
        duration = FoscDuration(duration);
        multiplier = FoscMultiplier(*referenceDuration.pair.reverse) * FoscMultiplier(60, unitsPerMinute);
        duration = (duration * multiplier).asFloat;
        ^duration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • listRelatedTempos (abjad: list_related_tempos)
    -------------------------------------------------------------------------------------------------------- */
    listRelatedTempos {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • unitDuration (not in abjad -- for compatibility with SuperCollider)

    a = FoscMetronomeMark([1, 4], 60);
    a.unitDuration.asFloat.postln;

    a = FoscMetronomeMark([1, 4], 120);
    a.unitDuration.asFloat.postln;
    -------------------------------------------------------------------------------------------------------- */
    unitDuration {
        ^this.durationToSeconds(FoscDuration(1, 4));
    }
    /* --------------------------------------------------------------------------------------------------------
    • unitTempo (not in abjad -- for compatibility with SuperCollider)

    a = FoscMetronomeMark([1, 4], 60);
    a.unitTempo.asFloat;

    a = FoscMetronomeMark([1, 4], 120);
    a.unitTempo;
    -------------------------------------------------------------------------------------------------------- */
    unitTempo {
        ^this.unitDuration.reciprocal.asFloat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prDotted
    -------------------------------------------------------------------------------------------------------- */
    prDotted {
        ^this.referenceDuration.lilypondDurationString;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prEquation
    
    a = FoscMetronomeMark(FoscDuration(1, 8), 96);
    a.prEquation;
    -------------------------------------------------------------------------------------------------------- */
    prEquation {
        var markup;
        if (this.referenceDuration.isNil) { ^nil };
        case 
        { this.unitsPerMinute.isSequenceableCollection } {
            ^"%=%-%".format(this.prDotted, this.unitsPerMinute[0], this.unitsPerMinute[1]);
        }
        { this.unitsPerMinute.isFloat || { this.unitsPerMinute.isKindOf(FoscFraction) } } {
            markup = FoscMetronomeMark.makeTempoEquationMarkup(this.referenceDuration, this.unitsPerMinute);
            ^markup.str;
        };
        ^"%=%".format(this.prDotted, this.unitsPerMinute);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    a = FoscMetronomeMark(FoscDuration(1, 8), 96);
    a.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        var text, equation;
        if (this.textualIndication.notNil) {
            text = this.textualIndication;
            text = FoscScheme.formatSchemeValue(text);
        };
        if (this.referenceDuration.notNil && { this.unitsPerMinute.notNil }) {
            equation = this.prEquation;
        };
        case
        { this.customMarkup.notNil } {
            ^"\\tempo %".format(this.customMarkup);
        }
        { text.notNil && { equation.notNil } } {
            ^"\\tempo % %".format(text, equation);
        }
        { equation.notNil } {
            ^"\\tempo %".format(equation);
        }
        { text.notNil } {
            ^"\\tempo %".format(text);
        }
        { ^"\\tempo \\default" };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
        var bundle;
        bundle = FoscLilyPondFormatBundle();
        if (hide.not) {
            bundle.before.commands.add(this.prGetLilypondFormat);
        };
        ^bundle;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • customMarkup
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isImprecise

    Is true if tempo is entirely textual or if tempo's units_per_minute is a range. Otherwise false.

    Returns true or false.

    
    FoscMetronomeMark(FoscDuration(1, 4), 60).isImprecise           // false

    FoscMetronomeMark(4, 60, 'Langsam').isImprecise                 // false

    FoscMetronomeMark(textualIndication: 'Langsam').isImprecise     // true

    FoscMetronomeMark(4, [35, 50], 'Langsam').isImprecise           // true

    FoscMetronomeMark(FoscDuration(1, 4), [35, 50]).isImprecise     // true
    -------------------------------------------------------------------------------------------------------- */
    isImprecise {
        if (this.referenceDuration.notNil) {
            if (this.unitsPerMinute.notNil) {
                if (this.unitsPerMinute.isSequenceableCollection.not) {
                    ^false;
                };
            };
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • quartersPerMinute
    
    a = FoscMetronomeMark(FoscDuration(1, 8), 60);
    a.quartersPerMinute;
    -------------------------------------------------------------------------------------------------------- */
    //!!!INCOMPLETE
    quartersPerMinute {
        var result;
        // if (this.isImprecise) { ^nil };
        result = referenceDuration / FoscDuration(1, 4) * unitsPerMinute;
        result = result.asFloat;
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • referenceDuration
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • textualIndication
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • unitsPerMinute
    -------------------------------------------------------------------------------------------------------- */
}
