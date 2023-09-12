/* ------------------------------------------------------------------------------------------------------------
• FoscRehearsalMark (abjad 3.0)

Rehearsal mark.



• Example 1

Initialize from number, display as letter.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(number: 1);
a.leafAt(0).attach(m);
set(a).markFormatter = FoscScheme('format-mark-box-alphabet');
a.show;


• Example 2

Initialize from number, display as number.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(number: 1);
a.leafAt(0).attach(m);
set(a).markFormatter = FoscScheme('format-mark-box-numbers');
a.show;


• Example 3

Initialize from markup.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(markup: 'A1');
a.leafAt(0).attach(m);
a.show;


• Example 4

Reharsal mark can be tweaked when markup is not nil.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(markup: "A1", tweaks: #['color', 'blue']);
a.leafAt(0).attach(m);
a.show;


• Example 5

Tweaks have no effect when markup is nil.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(number: 1, tweaks: #['color', 'blue']);
a.leafAt(0).attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscRehearsalMark : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <number, <markup, <tweaks;
    var <context='Score';
    *new { |number, markup, tweaks|
        assert([FoscMarkup, String, Symbol, Nil].any { |type| markup.isKindOf(type) });
        if (markup.notNil) { markup = FoscMarkup(markup) };
        assert([Integer, Nil].any { |type| number.isKindOf(type) });
        ^super.new.init(number, markup, tweaks);
    }
    init { |argNumber, argMarkup, argTweaks|
        number = argNumber;
        markup = argMarkup;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context of rehearsal mark. Returns 'Score'.


    • Example 1

    m = FoscRehearsalMark(number: 1);
    m.context.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • markup

    Gets rehearsal mark markup.


    • Example 1

    m = FoscRehearsalMark(markup: FoscMarkup("A1"));
    m.markup.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • number

    Gets rehearsal mark number.


    • Example 1

    m = FoscRehearsalMark(number: 1);
    m.number;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    m = FoscRehearsalMark(number: 1, tweaks: #[['color', 'red']]);
    m.tweaks.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Copies rehearsal mark.
    -------------------------------------------------------------------------------------------------------- */
    copy {
        var new;
        new = this.species.new(this);
        FoscLilyPondTweakManager.setTweaks(new, tweaks.copy);
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of rehearsal mark.


    • Example 1

    m = FoscRehearsalMark(number: 1);
    m.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.prGetLilypondFormat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        var result;
        case
        { markup.notNil } {
            result = "\\mark %".format(markup.str);
        }
        { number.notNil } {
            result = "\\mark #%".format(number);
        }
        {
            result = "\\mark \\default";
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions(directed: false);
            bundle.opening.commands.addAll(localTweaks);
        };
        bundle.opening.commands.add(this.prGetLilypondFormat);
        ^bundle;
    }
}
