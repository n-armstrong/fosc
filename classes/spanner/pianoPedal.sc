/* ------------------------------------------------------------------------------------------------------------
• pianoPedal (abjad 3.0)

Attaches piano pedal indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
set(a).pedalSustainStyle = 'mixed';
override(a).sustainPedalLineSpanner.staffPadding = 5;
a[0..].pianoPedal;
a.show;


• Example 2

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].pianoPedal(type: 'sostenuto');
a.show;


• Example 3

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].pianoPedal(type: 'corda');
a.show;


• Example 4

Piano pedals can be tweaked.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
set(a).pedalSustainStyle = 'mixed';
override(a).sustainPedalLineSpanner.staffPadding = 5;
a[0..].pianoPedal(tweaks: #[['color', 'blue']]);
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    //!!!TODO: remove startPianoPedal and stopPianoPedal arguments, just use 'type'
    // NB: 'type' argument not in abjad
    pianoPedal { |startPianoPedal, stopPianoPedal, type='sustain', tweaks|
        var leaves, startLeaf, stopLeaf;
        startPianoPedal = startPianoPedal ?? { FoscStartPianoPedal(type) };
        stopPianoPedal = stopPianoPedal ?? { FoscStopPianoPedal(type) };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        //!!! not in abjad
        if (startPianoPedal.tweaks.notNil) { tweaks = startPianoPedal.tweaks.addAll(tweaks) };
        FoscLilyPondTweakManager.setTweaks(startPianoPedal, tweaks);
        //!!!
        startLeaf.attach(startPianoPedal);
        stopLeaf.attach(stopPianoPedal);
    }
}
