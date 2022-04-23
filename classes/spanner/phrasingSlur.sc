/* ------------------------------------------------------------------------------------------------------------
• phrasingSlur (abjad 3.0)

Attaches phrasing slur indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].phrasingSlur;
a.show;


• Example 2

Phrasing slurs can be tweaked.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].phrasingSlur(tweaks: #[['color', 'blue']]);
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    phrasingSlur { |startPhrasingSlur, stopPhrasingSlur, tweaks|
        var leaves, startLeaf, stopLeaf;
        startPhrasingSlur = startPhrasingSlur ?? { FoscStartPhrasingSlur() };
        stopPhrasingSlur = stopPhrasingSlur ?? { FoscStopPhrasingSlur() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        //!!! not in abjad
        if (startPhrasingSlur.tweaks.notNil) { tweaks = startPhrasingSlur.tweaks.addAll(tweaks) };
        FoscLilyPondTweakManager.setTweaks(startPhrasingSlur, tweaks);
        //!!!
        startLeaf.attach(startPhrasingSlur);
        stopLeaf.attach(stopPhrasingSlur);
    }
}
