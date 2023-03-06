/* ------------------------------------------------------------------------------------------------------------
• tie (abjad 3.0)

Attaches tie indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]));
a[0..].tie;
a.show;


• Example 2

Ties consecutive chords if all adjacent pairs have at least one pitch in common.

a = FoscStaff(FoscLeafMaker().(#[[60],[60,62],[62]], [1/4]));
a[0..].tie;
a.show;


• Example 3

Same as example 2 but with tie above note on 2nd tie.

a = FoscStaff(FoscLeafMaker().(#[[60],[60,62],[62]], [1/4]));
a[0..1].tie;
a[1..2].tie(direction: 'up');
a.show;


• Example 4

Enharmonics are allowed.

a = FoscStaff(FoscLeafMaker().(#["C4", "B#3", "Dbb4"], [1/4]));
a[0..].tie;
a.show;


• Example 5

Ties can be tweaked.

a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]));
a[0..].tie(tweaks: #[['color', 'blue']]);
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    tie { |direction, repeat=false, tweaks|
        var inequality, leaves, duration, tie;
        
        leaves = this.leaves;
        //!!!TODO: leaves = this.leaves(doNotIterateGraceContainer: true);
        
        if (leaves.size < 2) { ^throw("Tie selection must contain two or more notes.") };

        leaves.do { |leaf|
            if ([FoscNote, FoscChord].any { |type| leaf.isKindOf(type) }.not) {
                ^throw("Attempt to tie a non-pitched leaf: %.".format(leaf));
            };
        };

        leaves.doAdjacentPairs { |leafA, leafB|
            duration = leafA.prGetDuration;
            leafA.detach(FoscTie);
            //!!! leafB.detach(FoscRepeatTie);
            tie = FoscTie(direction: direction);
            leafA.attach(tie);
            //!!! not in abjad
            if (tie.tweaks.notNil) { tweaks = tie.tweaks.addAll(tweaks) };
            FoscLilyPondTweakManager.setTweaks(tie, tweaks);
            //!! 
        };
    }
}
