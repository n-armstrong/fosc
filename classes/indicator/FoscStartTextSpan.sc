/* ------------------------------------------------------------------------------------------------------------
• FoscStartTextSpan (abjad 3.0)


• Example 1

Attaches text span indicators.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/4));
x = FoscStartTextSpan(
    leftText: FoscMarkup('pont.').upright,
    rightText: FoscMarkup('tasto').upright,
    style: 'solidLineWithArrow'
);
a[0..].textSpanner(x);
a.show;


• Example 2

Strings are automatically wrapped as markups.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/4));
x = FoscStartTextSpan("pont.", "tasto", 'solidLineWithArrow');
a[0..].textSpanner(x);
override(a).textSpanner.staffPadding = 4;
a.show;


• Example 3

Enchained spanners.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,nil], 2/4));
x = FoscStartTextSpan("pont.", style: 'solidLineWithArrow');
a[..2].textSpanner(x);
x = FoscStartTextSpan("tasto", "pont.", 'solidLineWithArrow');
a[2..].textSpanner(x);
override(a).textSpanner.staffPadding = 4;
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscStartTextSpan : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <styles;
    var <command, <concatHspaceLeft, <concatHspaceRight, <direction, <leftBrokenText, <leftText;
    var <rightPadding, <rightText, <style, <tweaks;
    //var <publishStorageFormat=true;
    *initClass {
        styles = #[
            'dashedLineWithArrow',
            'dashedLineWithHook',
            'invisibleLine',
            'solidLineWithArrow',
            'solidLineWithHook'
        ];
    }
    *new { |leftText, rightText, style, direction, command="\\startTextSpan", concatHspaceLeft=0.5, concatHspaceRight=0.5, leftBrokenText, rightPadding, tweaks|
        // assert isinstance(command, str), repr(command)
        // assert command.startswith('\\'), repr(command)
        // self._command = command
        // if concat_hspace_left is not None:
        //     assert isinstance(concat_hspace_left, (int, float))
        // self._concat_hspace_left = concat_hspace_left
        // if concat_hspace_right is not None:
        //     assert isinstance(concat_hspace_right, (int, float))
        // self._concat_hspace_right = concat_hspace_right
        // direction_ = String.to_tridirectional_lilypond_symbol(direction)
        // self._direction = direction_
        // if left_broken_text is not None:
        //     assert isinstance(left_broken_text, (bool, markups.Markup))
        // self._left_broken_text = left_broken_text
        // if left_text is not None:
        //     type = (LilyPondLiteral, markups.Markup)
        //     assert isinstance(left_text, type), repr(left_text)
        // self._left_text = left_text
        // if right_padding is not None:
        //     assert isinstance(right_padding, (int, float)), repr(right_padding)
        // self._right_padding = right_padding
        // if right_text is not None:
        //     type = (LilyPondLiteral, markups.Markup)
        //     assert isinstance(right_text, type), repr(right_text)
        // self._right_text = right_text
        // if style is not None:
        //     assert style in self._styles, repr(style)
        // self._style = style

        if ([String, Symbol].any { |type| leftText.isKindOf(type) }) {
            leftText = upright(FoscMarkup(leftText));
        };
        if ([String, Symbol].any { |type| rightText.isKindOf(type) }) {
            rightText = upright(FoscMarkup(rightText));
        };

        ^super.new.init(leftText, rightText, style, direction, command, concatHspaceLeft, concatHspaceRight,
            leftBrokenText, rightPadding, tweaks);
    }
    init { |argLeftText, argRightText, argStyle, argDirection, argCommand, argConcatHspaceLeft, 
        argConcatHspaceRight, argLeftBrokenText, argRightPadding, argTweaks|
        leftText = argLeftText;
        rightText = argRightText;
        style = argStyle;
        direction = argDirection;
        command = argCommand;
        concatHspaceLeft = argConcatHspaceLeft;
        concatHspaceRight = argConcatHspaceRight;
        leftBrokenText = argLeftBrokenText;
        rightPadding = argRightPadding;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • command

    Gets command.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • concatHspaceLeft

    Gets left hspace.

    Only included in LilyPond output when left text is set.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • concatHspaceRight

    Gets right hspace.

    Only included in LilyPond output when right text is set.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction

    Gets direction.


    • Example 1
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • enchained

    Returns true.


    • Example 1
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leftBrokenText

    Gets left broken text.


    • Example 1

    m = FoscRepeat();
    m.repeatType.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leftText

    Gets left text.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rightPadding

    Gets right padding.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rightText

    Gets right text.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart

    Is true.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • style

    Gets style.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAddDirection
    -------------------------------------------------------------------------------------------------------- */
    prAddDirection { |string|
        if (direction.notNil) { string = "% %".format(direction, string) };
        ^string;
    }   
    /* --------------------------------------------------------------------------------------------------------
    • prLeftBrokenTextTweak
    -------------------------------------------------------------------------------------------------------- */
    prLeftBrokenTextTweak {
        var override, string;
        override = FoscLilyPondGrobOverride(
            grobName: "TextSpanner",
            propertyPath: #['bound-details', 'left-broken', 'text'],
            value: leftBrokenText
        );
        string = override.tweakString;
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prLeftTextTweak
    -------------------------------------------------------------------------------------------------------- */
    prLeftTextTweak {
        var markup, concatHspaceLeftMarkup, markupList, override, string;
        if (leftText.isKindOf(FoscLilyPondLiteral)) {
            markup = leftText;
        } {
            concatHspaceLeftMarkup = FoscMarkup.hspace(concatHspaceLeft);
            markupList = [leftText, concatHspaceLeftMarkup];
            markup = FoscMarkup.concat(markupList);
        }; 
        override = FoscLilyPondGrobOverride(
            grobName: "TextSpanner",
            propertyPath: #['bound-details', 'left', 'text'],
            value: markup
        );
        string = override.tweakString;
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, string, localTweaks;
        bundle = FoscLilyPondFormatBundle();
        if (style.notNil) {
            string = this.prStyleTweak(style);
            bundle.after.spannerStarts.add(string);
        };
        if (leftText.notNil) {
            string = this.prLeftTextTweak;
            bundle.after.spannerStarts.add(string);
        };
        if (leftBrokenText.notNil) {
            string = this.prLeftBrokenTextTweak;
            bundle.after.spannerStarts.add(string);
        };
        if (rightText.notNil) {
            string = this.prRightTextTweak;
            bundle.after.spannerStarts.add(string);
        };
        if (rightPadding.notNil) {
            string = this.prRightPaddingTweak;
            bundle.after.spannerStarts.add(string);
        };
        if (tweaks.notNil) {
            localTweaks = this.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        string = this.prAddDirection(command);
        bundle.after.spannerStarts.add(string);
        
        ^bundle;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRightPaddingTweak
    -------------------------------------------------------------------------------------------------------- */
    prRightPaddingTweak {
        var override, string;
        override = FoscLilyPondGrobOverride(
            grobName: "TextSpanner",
            propertyPath: #['bound-details', 'right', 'padding'],
            value: rightPadding
        );
        string = override.tweakString(this);
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRightTextTweak
    -------------------------------------------------------------------------------------------------------- */
    prRightTextTweak {
        var number, concatHspaceRightMarkup, markupList, markup, override, string;
        if (concatHspaceRight.notNil) {
            number = concatHspaceRight;
            concatHspaceRightMarkup = FoscMarkup.hspace(number);
            markupList = [concatHspaceRightMarkup, rightText];
            markup = FoscMarkup.concat(markupList);
        } {
            markup = rightText;
        };
        override = FoscLilyPondGrobOverride(
            grobName: "TextSpanner",
            propertyPath: #['bound-details', 'right', 'text'],
            value: markup
        );
        string = override.tweakString;
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prStaffPaddingTweak
    -------------------------------------------------------------------------------------------------------- */
    prStaffPaddingTweak {
        // TODO ???
    }
    /* --------------------------------------------------------------------------------------------------------
    • prStartCommand
    -------------------------------------------------------------------------------------------------------- */
    prStartCommand {
        var string;
        string = this.prParameterName; //!!!TODO: where is this method defined ??
        ^"\\start%".format(string);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prStyleTweak
    -------------------------------------------------------------------------------------------------------- */
    prStyleTweak { |syle|
        var string, pieces, rightPadding;
        switch(style, 
            'dashedLineWithArrow', {
                pieces = #[
                    "- \\tweak Y-extent ##f",
                    "- \\tweak arrow-width 0.25",
                    "- \\tweak dash-fraction 0.25",
                    "- \\tweak dash-period 1.5",
                    "- \\tweak bound-details.left.stencil-align-dir-y #center",
                    "- \\tweak bound-details.left-broken.text ##f",
                    "- \\tweak bound-details.right.arrow ##t",
                    "- \\tweak bound-details.right.padding 0.5",
                    "- \\tweak bound-details.right.stencil-align-dir-y #center",
                    "- \\tweak bound-details.right-broken.arrow ##t",
                    "- \\tweak bound-details.right-broken.padding 0",
                    "- \\tweak bound-details.right-broken.text ##f"
                ];
            },
            'dashedLineWithHook', {
                pieces = #[
                    "- \\tweak Y-extent ##f",
                    "- \\tweak dash-fraction 0.25",
                    "- \\tweak dash-period 1.5",
                    "- \\tweak bound-details.left.stencil-align-dir-y #center",
                    "- \\tweak bound-details.left-broken.text ##f",
                    // right padding to avoid last leaf in spanner:
                    "- \\tweak bound-details.right.padding 1.25",
                    "- \\tweak bound-details.right.stencil-align-dir-y #up",
                    "- \\tweak bound-details.right.text \\markup { \\draw-line #'(0 . -1) }",
                    "- \\tweak bound-details.right-broken.arrow ##f",
                    "- \\tweak bound-details.right-broken.padding 0",
                    "- \\tweak bound-details.right-broken.text ##f"
                ];
            },
            'invisibleLine', {
                pieces = #[
                    "- \\tweak Y-extent ##f",
                    "- \\tweak dash-period 0",
                    "- \\tweak bound-details.left.stencil-align-dir-y #center",
                    "- \\tweak bound-details.left-broken.text ##f",
                    "- \\tweak bound-details.right.padding 0.5",
                    "- \\tweak bound-details.right.stencil-align-dir-y #center",
                    "- \\tweak bound-details.right-broken.padding 0",
                    "- \\tweak bound-details.right-broken.text ##f"
                ];
            },
            'solidLineWithArrow', {
                // pieces = ["- \\tweak arrow-length #0.7"];
                // pieces = pieces.add("- \\tweak arrow-width #0.2");
                // pieces = pieces.add("- \\tweak bound-details.left.stencil-align-dir-y #CENTER");
                // pieces = pieces.add("- \\tweak bound-details.right.arrow ##t");
                // if (concatHspaceRight.notNil && { concatHspaceRight > 0 }) {
                //     rightPadding = concatHspaceRight + 0.25;
                //     pieces = pieces.add("- \\tweak bound-details.right.padding #%".format(rightPadding));      
                // };
                // pieces = pieces.add("- \\tweak bound-details.right.stencil-align-dir-y #CENTER");
                // pieces = pieces.add("- \\tweak style #'line");
                pieces = #[
                    "- \\tweak Y-extent ##f",
                    "- \\tweak arrow-width 0.25",
                    "- \\tweak dash-fraction 1",
                    "- \\tweak bound-details.left.stencil-align-dir-y #center",
                    "- \\tweak bound-details.left-broken.text ##f",
                    "- \\tweak bound-details.right.arrow ##t",
                    "- \\tweak bound-details.right.padding 0.5",
                    "- \\tweak bound-details.right.stencil-align-dir-y #center",
                    "- \\tweak bound-details.right-broken.padding 0",
                    "- \\tweak bound-details.right-broken.text ##f"
                ];
            },
            'solidLineWithHook', {
                pieces = #[
                    "- \\tweak Y-extent ##f",
                    "- \\tweak dash-fraction 1",
                    "- \\tweak bound-details.left.stencil-align-dir-y #center",
                    "- \\tweak bound-details.left-broken.text ##f",
                    // right padding to avoid last leaf in spanner:
                    "- \\tweak bound-details.right.padding 1.25",
                    "- \\tweak bound-details.right.stencil-align-dir-y #up",
                    "- \\tweak bound-details.right.text \\markup { \\draw-line #'(0 . -1) }",
                    "- \\tweak bound-details.right-broken.arrow ##f",
                    "- \\tweak bound-details.right-broken.padding 0",
                    "- \\tweak bound-details.right-broken.text ##f"
                ];
            }
        );
        string = pieces.join("\n");
        ^string;
    }
}
