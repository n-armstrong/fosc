/* ------------------------------------------------------------------------------------------------------------
• horizontalBracket (abjad 3.0)

Attaches group indicators.


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

Partition selection by sizes, bracket each new selection, add annotations.

a = FoscStaff(FoscLeafMaker().((60..75), [1/32]));
a.consistsCommands.add('Horizontal_bracket_engraver');
t = #[['bracket-flare', [0,0]], ['direction', 'up'], ['staff-padding', 3]];
a[0..].partitionBySizes(#[3,4,6,3]).do { |sel, i|
    sel.horizontalBracket(tweaks: t);
    sel[0].attach(FoscMarkup((i + 1).asString, 'up', tweaks: #['font-size', -1, 'color', 'blue']));
};
a[0..].beam(startBeam: FoscStartBeam(direction: 'down'));
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    horizontalBracket { |startGroup, stopGroup, tag, tweaks|
        var leaves, startLeaf, stopLeaf;
        startGroup = startGroup ?? { FoscStartGroup() };
        stopGroup = stopGroup ?? { FoscStopGroup() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        //!!! not in abjad
        if (startGroup.tweaks.notNil) { tweaks = startGroup.tweaks.addAll(tweaks) };
        FoscLilypondTweakManager.setTweaks(startGroup, tweaks);
        //!!!
        startLeaf.attach(startGroup, tag: tag);
        stopLeaf.attach(stopGroup, tag: tag);
    }
}
