/* ------------------------------------------------------------------------------------------------------------
• horizontalBracket (abjad 3.0)

Attaches group indicators.

textAlign: [lilypond: parent-alignment-X]: Specify on which point of the parent the object is aligned. The value -1 means aligned on parent’s left edge, 0 on center, and 1 right edge, in X direction. Other numerical values may also be specified - the unit is half the parent’s width. If unset, the value from self-alignment-X property will be used.



• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.consistsCommands.add('Horizontal_bracket_engraver');
a[0..].horizontalBracket;
a.show;
a.format;


• Example 2

Horizontal bracket can be tweaked.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a.consistsCommands.add('Horizontal_bracket_engraver');
t = #['bracket-flare', [0,0], 'color', 'red', 'direction', 'up', 'staff-padding', 3];
a[0..].horizontalBracket(tweaks: t);
a.show;


• Example 3

Partition selection by sizes, bracket each new selection, add text annotation.

a = FoscStaff(FoscLeafMaker().((60..75), [1/32]));
t = #[['bracket-flare', [0,0]], ['direction', 'up'], ['staff-padding', 0]];

a[0..].partitionBySizes(#[3,4,6,3]).do { |sel, i|
    sel.horizontalBracket(text: FoscMarkup(sel.size.asString).fontSize(-2), textAlign: -0.8, tweaks: t);
};

a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    horizontalBracket { |startGroup, stopGroup, text, textAlign=0, tweaks|
        var leaves, startLeaf, stopLeaf, tweak;

        startGroup = startGroup ?? { FoscStartGroup() };
        stopGroup = stopGroup ?? { FoscStopGroup() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;

        if (text.notNil) {
            if (tweaks.isNil) {
                tweaks = [];
            } {
                tweaks = tweaks.copy;
            };

            text = FoscMarkup(text);

            tweaks = tweaks.addAll([
                [FoscLilyPondLiteral("HorizontalBracketText.text"), text],
                [FoscLilyPondLiteral("HorizontalBracketText.parent-alignment-X"), textAlign]
            ]);
        };

        if (startGroup.tweaks.notNil) { tweaks = startGroup.tweaks.addAll(tweaks) };
        FoscLilyPondTweakManager.setTweaks(startGroup, tweaks);
        
        startLeaf.attach(startGroup);
        stopLeaf.attach(stopGroup);
    }
}
