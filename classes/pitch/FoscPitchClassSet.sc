/* ------------------------------------------------------------------------------------------------------------
• FoscPitchClassSet

Pitch-class set.

### INCOMPLETE
### abjad.PitchClassSet subclasses from Set

a = FoscPitchClassSet[0, 2, 4, 8];

a = FoscPitchClassSet(#[0, 2, 4, 8]);
a.items;

a = FoscPitchClassSet([0, 2, 4, 8]);
b = FoscPitchClassSet([1, 3, 4, 8]);
a.sect(b).do { |each| each.pitchClass.number.postln };

b = FoscPitchSet([60, 61, 62]);
a = FoscPitchClassSet(b);
a.items.do { |each| each.pitchClass.number.postln }
------------------------------------------------------------------------------------------------------------ */
FoscPitchClassSet : FoscTypedSet {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |items|
        //### some of the below is redundant -- it's handled in the superclasses
        if ([FoscPitchClassSet, FoscPitchSet, FoscPitchSegment].any { |type| items.isKindOf(type) }) {
            items = items.items;
            items = items.collect { |each| FoscPitchClass(each) };
        } {
           if (items.isSequenceableCollection.not) { items = [items] };
           items = items.collect { |each| FoscPitchClass(each) };
           items = items.sort { |a, b| a.pitchClass.number < b.pitchClass.number };
        };
        //### assert(all in items isKindOf(FoscPitchClass))
        ^super.new(items, FoscPitchClass);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • includes (abjad: __contains__)

    Is true when pitch-class set contains `argument`. Otherwise false.
            
    Returns true or false.
    
    def __contains__(self, argument):
        return super(PitchClassSet, self).__contains__(argument)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes pitch-class set.

    Returns integer.

    def __hash__(self):
        return super(PitchClassSet, self).__hash__()
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates pitch-class set.
    
    def __illustrate__(self):
        import abjad
        chord = abjad.Chord(self, abjad.Duration(1))
        voice = abjad.Voice([chord])
        staff = abjad.Staff([voice])
        score = abjad.Score([staff])
        lilypond_file = abjad.LilyPondFile.new(score)
        lilypond_file.header_block.tagline = False
        return lilypond_file
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of pitch-class set.

    Returns string.
    
    def __str__(self):
        import abjad
        items = [str(_) for _ in sorted(self)]
        separator = ' '
        if self.item_class is abjad.NumberedPitchClass:
            separator = ', '
        return 'PC{{{}}}'.format(separator.join(items))
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* --------------------------------------------------------------------------------------------------------
    •
    
    @property
    def _named_item_class(self):
        from abjad.tools import pitchtools
        return pitchtools.NamedPitchClass
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    
    @property
    def _numbered_item_class(self):
        from abjad.tools import pitchtools
        return pitchtools.NumberedPitchClass
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    
    @property
    def _parent_item_class(self):
        from abjad.tools import pitchtools
        return pitchtools.PitchClass
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    
    @staticmethod
    def _get_most_compact_ordering(candidates):
        from abjad.tools import pitchtools
        widths = []
        for candidate in candidates:
            if candidate[0] < candidate[-1]:
                width = abs(candidate[-1] - candidate[0])
            else:
                width = abs(candidate[-1] + 12 - candidate[0])
            widths.append(width)
        minimum_width = min(widths)
        candidates_ = []
        for candidate, width in zip(candidates, widths):
            if width == minimum_width:
                candidates_.append(candidate)
        candidates = candidates_
        assert 1 <= len(candidates)
        if len(candidates) == 1:
            segment = candidates[0]
            segment = pitchtools.PitchClassSegment(
                items=segment,
                item_class=pitchtools.NumberedPitchClass,
                )
            return segment
        for i in range(len(candidates[0]) - 1):
            widths = []
            for candidate in candidates:
                if candidate[0] < candidate[i+1]:
                    width = abs(candidate[i+1] - candidate[0])
                else:
                    width = abs(candidate[i+1] + 12 - candidate[0])
                widths.append(width)
            minimum_width = min(widths)
            candidates_ = []
            for candidate, width in zip(candidates, widths):
                if width == minimum_width:
                    candidates_.append(candidate)
            candidates = candidates_
            if len(candidates) == 1:
                segment = candidates[0]
                segment = pitchtools.PitchClassSegment(
                    items=segment,
                    item_class=pitchtools.NumberedPitchClass,
                    )
                return segment
        candidates.sort(key=lambda x: x[0])
        segment = candidates[0]
        segment = pitchtools.PitchClassSegment(
            items=segment,
            item_class=pitchtools.NumberedPitchClass,
            )
        return segment
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    
    def _sort_self(self):
        from abjad.tools import pitchtools
        def helper(x, y):
            return cmp(
                pitchtools.NamedPitch(pitchtools.NamedPitchClass(x), 0),
                pitchtools.NamedPitch(pitchtools.NamedPitchClass(y), 0)
                )
        result = list(self)
        result.sort(helper)
        return result
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* --------------------------------------------------------------------------------------------------------
    • *newFromSelection

    Makes pitch-class set from `selection`.
        
    Returns pitch-class set.
    
    @classmethod
    def from_selection(
        class_,
        selection,
        item_class=None,
        ):
        from abjad.tools import pitchtools
        pitch_segment = pitchtools.PitchSegment.from_selection(selection)
        return class_(
            items=pitch_segment,
            item_class=item_class,
            )
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • difference

    Set-theoretic difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.difference(b).do { |each| each.pitchClass.number.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Set-theoretic intersection of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.sect(b).do { |each| each.pitchClass.number.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isDisjoint

    Is true when typed receiver shares no elements with expr. Otherwise false.

    Returns boolean.

    a = FoscPitchClassSet([1, 2, 3], Number);
    b = FoscPitchClassSet([4], Number);
    c = FoscPitchClassSet([3, 4], Number);
    isDisjoint(a, b);
    isDisjoint(a, c);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    Is true when pitch class set is empty.

    Returns boolean.

    a = FoscPitchClassSet([1, 2, 3], Number);
    a.isEmpty;

    a = FoscPitchClassSet([], Number);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSubsetOf

    Is true when receiver is a subset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3]);
    a.isSubsetOf(b);
    b.isSubsetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSupersetOf

    Is true when receiver is a superset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3]);
    a.isSupersetOf(b);
    b.isSupersetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    Is true when set is not empty.

    Returns boolean.

    a = FoscPitchClassSet([1, 2, 3], Number);
    a.notEmpty;

    a = FoscPitchClassSet([], Number);
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Symmetric difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.symmetricDifference(b).do { |each| each.pitchClass.number.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.union(b).do { |each| each.pitchClass.number.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • normalOrder

    Gets normal order.

    Returns pitch-class segment.
    
    def get_normal_order(self):
        import abjad
        if not len(self):
            return abjad.PitchClassSegment(
                items=None,
                item_class=abjad.NumberedPitchClass,
                )
        pitch_classes = list(self)
        pitch_classes.sort()
        candidates = []
        for i in range(self.cardinality):
            candidate = [abjad.NumberedPitch(_) for _ in pitch_classes]
            candidate = abjad.Sequence(candidate).rotate(n=-i)
            candidates.append(candidate)
        return self._get_most_compact_ordering(candidates)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • primeForm

    Gets prime form.

    Returns new pitch-class set.
    
    def get_prime_form(self, transposition_only=False):
        import abjad
        if not len(self):
            return copy.copy(self)
        normal_orders = [self.get_normal_order()]
        if not transposition_only:
            inversion = self.invert()
            normal_order = inversion.get_normal_order()
            normal_orders.append(normal_order)
        normal_order = self._get_most_compact_ordering(normal_orders)
        pcs = [_.number for _ in normal_order]
        first_pc = pcs[0]
        pcs = [pc - first_pc for pc in pcs]
        prime_form = type(self)(
            items=pcs,
            item_class=abjad.NumberedPitchClass,
            )
        return prime_form
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • invert

    Inverts pitch-class set.
        
    Returns numbered pitch-class set.
    
    def invert(self, axis=None):
        return type(self)([pc.invert(axis=axis) for pc in self])
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • isTransposedSubset

    Is true when pitch-class set is transposed subset of `pcset`. Otherwise false.

    Returns true or false.
    
    def is_transposed_subset(self, pcset):
        for n in range(12):
            if self.transpose(n).issubset(pcset):
                return True
        return False
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • isTransposedSuperset

    Is true when pitch-class set is transposed superset of `pcset`. Otherwise false.

    Returns true or false.
    
    def is_transposed_superset(self, pcset):
        for n in range(12):
            if self.transpose(n).issuperset(pcset):
                return True
        return False
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • multiply

    Multiplies pitch-class set by `n`.

    Returns new pitch-class set.
    
    def multiply(self, n):
        import abjad
        items = (pitch_class.multiply(n) for pitch_class in self)
        return abjad.new(self, items=items)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • orderBy

    Orders pitch-class set by pitch-class `segment`.

    Returns pitch-class segment.
    
    def order_by(self, segment):
        import abjad
        if not len(self) == len(segment):
            message = 'set and segment must be on equal length.'
            raise ValueError(message)
        enumerator = abjad.Enumerator(self)
        for pitch_classes in enumerator.yield_permutations():
            candidate = abjad.PitchClassSegment(pitch_classes)
            if candidate._is_equivalent_under_transposition(segment):
                return candidate
        message = '{!s} can not order by {!s}.'
        message = message.format(self, segment)
        raise ValueError(message)
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes all pitch-classes in pitch-class set by index `n`.
    
    Returns new pitch-class set.
    
    def transpose(self, n=0):
        import abjad
        items = (pitch_class + n for pitch_class in self)
        return abjad.new(self, items=items)
    -------------------------------------------------------------------------------------------------------- */
}
