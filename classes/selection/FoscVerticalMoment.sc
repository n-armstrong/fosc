/* ------------------------------------------------------------------------------------------------------------
• FoscVerticalMoment

Vertical moment of a component.


• Example 1

g = FoscStaffGroup();
g.lilypondType = "PianoStaff";
g.add(FoscStaff(FoscLeafMaker().("C4 D4 E4 F4", [1/4])));
g.add(FoscStaff(FoscLeafMaker().("G3 F3", [2/4])));
g[1][0].attach(FoscClef('bass'));
x = FoscScore([g]);
x.show;

m = g.prVerticalMomentAt(FoscOffset(2/4));
m.attackCount;
m.startLeaves.do { |each| each.inspect };
m.startNotes.do { |each| each.inspect };
m.overlapLeaves.do { |each| each.inspect };
m.leaves.items;

##### BUG ######
FoscIteration(g[0]).leaves.do { |e|
    [e, e.prGetTimespan.startOffset.pair, e.prGetTimespan.stopOffset.pair].postln;
    g.prVerticalMomentAt(e.prGetTimespan.startOffset).startNotes.postln;
    Post.nl;
};
------------------------------------------------------------------------------------------------------------ */
FoscVerticalMoment : FoscSelection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    def __init__(self, items=None, offset=None):
        self._items = items
        if items is None:
            self._offset = offset
            self._components = ()
            self._governors = ()
            return
        governors, components = self._from_offset(items, offset)
        offset = durationtools.Offset(offset)
        self._offset = offset
        assert isinstance(governors, collections.Iterable)
        governors = tuple(governors)
        self._governors = governors
        assert isinstance(components, collections.Iterable)
        components = list(components)
        components.sort(key=lambda _: _._get_parentage().score_index)
        self._components = tuple(components)
    -------------------------------------------------------------------------------------------------------- */
    var <components, <governors, <offset;
    *new { |items, offset|
        ^super.new(items).initFoscVerticalMoment(offset);
    }
    initFoscVerticalMoment { |argOffset|
        var order;
        offset = FoscOffset(argOffset);
        # governors, components = FoscVerticalMoment.prFromOffset(items, offset);
        // assert(governors.isKindOf(SequenceableCollection))
        // assert(components.isKindOf(SequenceableCollection))
        // components.sort(key=lambda _: _._get_parentage().score_index)
        order = components.collect { |each| each.prGetParentage.scoreIndex }.orderN;
        components = components[order];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when argument is a vertical moment with the same components as this vertical moment. Otherwise false.
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        if (expr.isKindOf(FoscVerticalMoment)) {
            if (this.size == expr.size) {
                components.do { |component, i|
                    if (component != expr[i]) { ^false } { ^true };
                };
            };
        };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hases vertical moment.
    
    Returns integer.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • size

    Length of vertical moment.

    Defined equal to the number of components in vertical moment.

    Returns nonnegative integer.
    
    def __len__(self):
        return len(self.components)
    -------------------------------------------------------------------------------------------------------- */
    size {
        ^components.size;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString (abjad: __repr__)

    Gets interpreter representation of vertical moment.
    
    Returns string.
    
    def __repr__(self):
        if not self.components:
            return '{}()'.format(type(self).__name__)
        result = '{}({}, <<{}>>)'
        result = result.format(
            type(self).__name__,
            str(self.offset),
            len(self.leaves),
            )
        return result
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prFindIndex

    Based off Python's bisect.bisect() function.
    -------------------------------------------------------------------------------------------------------- */
    *prFindIndex { |container, offset|
        var lo, hi, mid, startOffset, stopOffset;
        lo = 0;
        hi = container.size;
        while { lo < hi } {
            mid = (lo + hi).div(2);
            startOffset = container[mid].prGetTimespan.startOffset;
            stopOffset = container[mid].prGetTimespan.stopOffset;
            case 
            { startOffset <= offset && { offset < stopOffset } } {
                lo = mid + 1;
            }
            // if container[mid] is of nonzero duration
            { startOffset < stopOffset } {
                hi = mid;
            }
            // container[mid] is of zero duration so we skip it
            {
                lo = mid + 1;
            };
        };
        ^(lo - 1);
        //^lo;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prFromOffset
    -------------------------------------------------------------------------------------------------------- */
    *prFromOffset { |expr, offset|
        var governors, type, message, order, components;
        governors = [];
        type = [SequenceableCollection, FoscSelection];
        message = "must be component or of Fosc components: %.".format(expr);
        case
        { expr.isKindOf(FoscComponent) } {
            governors = governors.add(expr);
        }
        { type.any { |type| expr.isKindOf(type) } } {
            expr.do { |each|
                if (each.isKindOf(FoscComponent)) {
                    governors = governors.add(each);
                } {
                    ^throw("%:%: type error: %.".format(this.species, thisMethod.name, message));
                };
            };
        }
        {
            ^throw("%:%: type error: %.".format(this.species, thisMethod.name, message))
        };
        // governors.sort(key=lambda x: x._get_parentage().score_index)
        order = governors.collect { |each| each.prGetParentage.scoreIndex }.orderN;
        governors = governors[order];
        components = [];
        governors.do { |governor|
            components = components.addAll(FoscVerticalMoment.prRecurse(governor, offset));
        };
        // components.sort(key=lambda x: x._get_parentage().score_index)
        order = components.collect { |each| each.prGetParentage.scoreIndex }.orderN;
        components = components[order];
        ^[governors, components];
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prRecurse
    
    g = FoscStaffGroup();
    a = FoscStaff([FoscLeafMaker().([60, 62, 52, 53, nil, 61, 59, 50, 66], [[1, 8]])]);
    a.name_('RHStaff');
    b = FoscStaff(a.items.copy.collect { |each| FoscSkip(each) });
    b.name_('LHStaff');
    b.attach(FoscClef('bass'));
    g.addAll([a, b]);
    FoscStaffChangeSpecifier("C4").value(g);

    g.prVerticalMomentAt(FoscOffset(1, 1));

    a[0]

    FoscVerticalMoment.prFindIndex(a[0], 0);
    FoscVerticalMoment.prFindIndex(a[8], [9, 8]);
    -------------------------------------------------------------------------------------------------------- */
    *prRecurse { |component, offset|
        var result, child;
        result = [];
        if (component.prGetTimespan.startOffset <= offset && { offset < component.prGetTimespan.stopOffset }) {
            result = result.add(component);
            if (component.respondsTo('items')) {
                if (component.isSimultaneous) {
                    component.do { |each| result = result.addAll(FoscVerticalMoment.prRecurse(each, offset)) };
                } {
                    child = component[FoscVerticalMoment.prFindIndex(component, offset)];
                    result = result.addAll(FoscVerticalMoment.prRecurse(child, offset));
                };
            };
        };
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • attackCount

    Positive integer number of pitch carriers starting at vertical moment.
    -------------------------------------------------------------------------------------------------------- */
    attackCount {
        var attackCarriers = [];
        this.startLeaves.do { |leaf|
            if ([FoscNote, FoscChord].any { |type| leaf.isKindOf(type) }) {
                attackCarriers = attackCarriers.add(leaf);
            };
        };
        ^attackCarriers.size;
    }
    /* --------------------------------------------------------------------------------------------------------
    • components

    Tuple of zero or more components happening at vertical moment.
    
    It is always the case that self.components = self.overlap_components + self.start_components.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • governors

    Tuple of one or more containers in which vertical moment is evaluated.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leaves

    Tuple of zero or more leaves at vertical moment.
    -------------------------------------------------------------------------------------------------------- */
    leaves {
        var result;
        result = [];
        components.do { |each| if (each.isKindOf(FoscLeaf)) { result = result.add(each) } };
        result = FoscSelection(result);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • items

    Gets items of vertical moment.
    
    Returns component or selection.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • nextVerticalMoment

    Reference to next vertical moment forward in time.
    -------------------------------------------------------------------------------------------------------- */
    nextVerticalMoment {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • notes

    Tuple of zero or more notes at vertical moment.
    -------------------------------------------------------------------------------------------------------- */
    notes {
        var result;
        result = [];
        components.do { |each| if (each.isKindOf(FoscNote)) { result = result.add(each) } };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • notesAndChords

    Tuple of zero or more notes and chords at vertical moment.
    -------------------------------------------------------------------------------------------------------- */
    notesAndChords {
        var type, result;
        type = [FoscNote, FoscChord];
        result = [];
        components.do { |each|
            if (type.any { |type| each.isKindOf(type) }) { result = result.add(each) };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • offset

    Rational-valued score offset at which vertical moment is evaluated.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • overlapComponents

    Tuple of components in vertical moment starting before vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    overlapComponents {
        var result;
        result = [];
        components.do { |each|
            if (each.prGetTimespan.startOffset < this.offset) { result = result.add(each) };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapLeaves

    Tuple of leaves in vertical moment starting before vertical moment, ordered by score index.

    -------------------------------------------------------------------------------------------------------- */
    overlapLeaves {
        var result;
        result = [];
        this.overlapComponents.do { |each| if (each.isKindOf(FoscLeaf)) { result = result.add(each) } };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapMeasures

    Tuple of measures in vertical moment starting before vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    overlapMeasures {
        var result;
        result = [];
        this.overlapComponents.do { |each| if (each.isKindOf(FoscMeasure)) { result = result.add(each) } };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapNotes

    Tuple of notes in vertical moment starting before vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    overlapNotes {
        var result;
        result = [];
        this.overlapComponents.do { |each| if (each.isKindOf(FoscNote)) { result = result.add(each) } };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • previousVerticalMoment

    Reference to previous vertical moment backward in time.
    
    @property
    def previous_vertical_moment(self):
        from abjad.tools import scoretools
        if self.offset == 0:
            raise IndexError
        most_recent_start_offset = durationtools.Offset(0)
        token_leaf = None
        for leaf in self.leaves:
            #print ''
            #print leaf
            leaf_start = leaf._get_timespan().start_offset
            if leaf_start < self.offset:
                #print 'found leaf starting before this moment ...'
                if most_recent_start_offset <= leaf_start:
                    most_recent_start_offset = leaf_start
                    token_leaf = leaf
            else:
                #print 'found leaf starting on this moment ...'
                try:
                    previous_leaf = leaf._get_in_my_logical_voice(
                        -1, type=scoretools.Leaf)
                    start = previous_leaf._get_timespan().start_offset
                    #print previous_leaf, start
                    if most_recent_start_offset <= start:
                        most_recent_start_offset = start
                        token_leaf = previous_leaf
                except IndexError:
                    pass
        #print 'token_leaf is %s ...' % token_leaf
        if token_leaf is None:
            token_leaf = leaf
            #print 'token_leaf is %s ...' % token_leaf
        previous_vertical_moment = token_leaf._get_vertical_moment()
        return previous_vertical_moment
    -------------------------------------------------------------------------------------------------------- */
    previousVerticalMoment {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • startComponents

    Tuple of components in vertical moment starting with at vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    startComponents {
        var result;
        result = [];
        components.do { |component|
            if (component.prGetTimespan.startOffset == this.offset) {
                result = result.add(component);
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • startLeaves

    Tuple of leaves in vertical moment starting with vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    startLeaves {
        var result;
        result = [];
        this.startComponents.do { |component|
            if (component.isKindOf(FoscLeaf)) { result = result.add(component) };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • startNotes

    Tuple of notes in vertical moment starting with vertical moment, ordered by score index.
    -------------------------------------------------------------------------------------------------------- */
    startNotes {
        var result;
        result = [];
        this.startComponents.do { |each| if (each.isKindOf(FoscNote)) { result = result.add(each) } };
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //!!!TODO: DEPRECATE THE FOLLOWING METHODS: use 'instVarPut' instead
    /* --------------------------------------------------------------------------------------------------------
    • prComponents_
    -------------------------------------------------------------------------------------------------------- */
    prComponents_ { |argComponents|
        components = argComponents;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGovernors_
    -------------------------------------------------------------------------------------------------------- */
    prGovernors_ { |argGovernors|
        governors = argGovernors;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prOffset_
    -------------------------------------------------------------------------------------------------------- */
    prOffset_ { |argOffset|
        offset = argOffset;
    }
}
