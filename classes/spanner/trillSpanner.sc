/* --------------------------------------------------------------------------------------------------------
• trillSpanner (abjad 3.0)

Trill spanner.


• Example 1

Attached unpitched trill spanner to all notes in staff.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
a[0..].trillSpanner;
a.show;


• Example 2

Attached pitched trill spanner to all notes in staff.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
a[0..].trillSpanner(pitch: "C#4");
a.show;


• Example 3

Attached pitched trill spanner to all notes in staff.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
a[0..].trillSpanner(interval: 1);
a.show;


• Example 4

Pitched trill spanner must appear after markup to avoid hiding markup in output.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
n = FoscMarkup('Allegro', direction: 'up');
a[0].attach(n);
a[0..].trillSpanner;
a.show;
-------------------------------------------------------------------------------------------------------- */
+ FoscSelection {
    //!!!TODO: pitch and interval args not in abjad
    trillSpanner { |startTrillSpan, stopTrillSpan, pitch, interval, tweaks|
        var leaves, startLeaf, stopLeaf;
        startTrillSpan = startTrillSpan ?? { FoscStartTrillSpan(pitch: pitch, interval: interval) };
        stopTrillSpan = stopTrillSpan ?? { FoscStopTrillSpan() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        //!!! not in abjad
        if (startTrillSpan.tweaks.notNil) { tweaks = startTrillSpan.tweaks.addAll(tweaks) };
        FoscLilyPondTweakManager.setTweaks(startTrillSpan, tweaks);
        //!!!
        startLeaf.attach(startTrillSpan);
        stopLeaf.attach(stopTrillSpan);
    }
}