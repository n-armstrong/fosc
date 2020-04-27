/* ------------------------------------------------------------------------------------------------------------
• textSpanner (abjad 3.0)

Attaches text span indicators.


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

a.format;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    textSpanner { |startTextSpan, stopTextSpan|
        var leaves, startLeaf, stopLeaf;
        startTextSpan = startTextSpan ?? { FoscStartTextSpan() };
        stopTextSpan = stopTextSpan ?? { FoscStopTextSpan() };
        leaves = this.leaves;
        startLeaf = leaves.first;
        stopLeaf = leaves.last;
        startLeaf.attach(startTextSpan);
        stopLeaf.attach(stopTextSpan);
    }
}
