/* ------------------------------------------------------------------------------------------------------------
• slur (abjad 3.0)

Attaches slur indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].slur;
a.show;


• Example 2

Phrasing slurs can be tweaked.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].slur(tweaks: #[['color', 'blue']]);
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    slur { |startSlur, stopSlur, tweaks|
        var leaves, startLeaf, stopLeaf;
        startSlur = startSlur ?? { FoscStartSlur() };
        stopSlur = stopSlur ?? { FoscStopSlur() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        //!!! not in abjad
        if (startSlur.tweaks.notNil) { tweaks = startSlur.tweaks.addAll(tweaks) };
        FoscLilyPondTweakManager.setTweaks(startSlur, tweaks);
        //!!!
        startLeaf.attach(startSlur);
        stopLeaf.attach(stopSlur);
    }
}
