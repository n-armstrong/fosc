/* ------------------------------------------------------------------------------------------------------------
• ottava (abjad 3.0)

Attaches ottava indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].ottava;
a.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    ottava { |startOttava, stopOttava, tag|
        var leaves, startLeaf, stopLeaf;
        startOttava = startOttava ?? { FoscOttava(n: 1) };
        stopOttava = stopOttava ?? { FoscOttava(n: 0, formatSlot: 'after') };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        startLeaf.attach(startOttava);
        stopLeaf.attach(stopOttava);
    }
}
