/* ------------------------------------------------------------------------------------------------------------
• FoscInspection

Inspection agent.
------------------------------------------------------------------------------------------------------------ */
FoscInspection : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    def __init__(self, client=None):
            from abjad.tools import scoretools
            from abjad.tools import spannertools
            type = (scoretools.Component, spannertools.Spanner, type(None))
            if not isinstance(client, type):
                message = 'must be component, spanner or none: {!r}.'
                message = message.format(client)
                raise TypeError(message)
            self._client = client
    
    a = FoscInspection('blerk');

    a = FoscInspection(FoscNote(60, 1));
    a.client;
    -------------------------------------------------------------------------------------------------------- */
    var <client;
    *new { |client|
        var type;
        //type = [FoscComponent, FoscSpanner, Nil];
        type = [FoscComponent, Nil];
        if (type.any { |type| client.isKindOf(type) }.not) {
            ^throw("%: client must be component, spanner, or nil: %.".format(this.species, client));
        };
        ^super.new.init(client);
    }
    init { |argClient|
        client = argClient;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • afterGraceContainer

    Gets after grace containers attached to leaf.

    Returns after grace container or none.
    
    abjad 2.21

    def get_after_grace_container(self):
        return self._client._after_grace_container
    -------------------------------------------------------------------------------------------------------- */
    afterGraceContainer {
        ^this.client.afterGraceContainer;
    }
    /* --------------------------------------------------------------------------------------------------------
    • annotation

    Gets value of annotation with name attached to client.

    Returns default when no annotation with name is attached to client.
    
    Raises exception when more than one annotation with name is attached to client.


    a = FoscNote(60, 1/4);
    a.annotate('clef', FoscClef('bass'));
    FoscInspection(a).annotation('clef');
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    annotation { |annotation, default, unwrap=true|
        this.annotationWrappers.do { |wrapper|
            if (wrapper.annotation == annotation) {
                if (unwrap) { ^wrapper.indicator } { ^wrapper };
            };
        };
        ^default
    }
    /* --------------------------------------------------------------------------------------------------------
    • annotationWrappers

    a = FoscNote(60, 1/4);
    a.annotate('clef', FoscClef('bass'));
    FoscInspection(a).annotationWrappers;
    -------------------------------------------------------------------------------------------------------- */
    annotationWrappers {
        var result;
        result = [];
        if (client.respondsTo('wrappers') && { client.wrappers.notNil }) {
            client.wrappers.do { |wrapper|
                if (wrapper.annotation.notNil) { result = result.add(wrapper) }; 
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • badlyFormedComponents

    Gets badly formed components in client.

    Returns array.
    
    staff = Staff("c'8 d'8 e'8 f'8")
    staff[1].written_duration = Duration(1, 4)
    beam = spannertools.Beam()
    attach(beam, staff[:])
    
    f(staff)
    \new Staff {
        c'8 [
        d'4
        e'8
        f'8 ]
    }
    
    inspect_(staff).get_badly_formed_components()
    [Note("d'4")]
    
    (Beamed quarter notes are not well formed.)
    

    def get_badly_formed_components(self):
        from abjad.tools import systemtools
        manager = systemtools.WellformednessManager()
        violators = []
        for current_violators, total, check_name in manager(self._client):
            violators.extend(current_violators)
        return violators
    -------------------------------------------------------------------------------------------------------- */
    badlyFormedComponents {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • components

    Gets all components of type in the descendants of client.
    
    Returns client selection.

    def get_components(
        self,
        type=None,
        include_self=True,
        ):
        return self._client._get_components(
            type=type,
            include_self=include_self,
            )
    -------------------------------------------------------------------------------------------------------- */
    components { |type, includeSelf=true|
        ^client.prComponents(type: type, includeSelf: includeSelf);
    }
    /* --------------------------------------------------------------------------------------------------------
    • contents

    Gets contents of client.
    
    Returns sequential selection.
    
    def get_contents(
        self,
        include_self=True,
        ):
        return self._client._get_contents(
            include_self=include_self,
            )
    -------------------------------------------------------------------------------------------------------- */
    contents { |includeSelf=true|
        ^client.prGetContents(includeSelf: includeSelf);
    }
    /* --------------------------------------------------------------------------------------------------------
    • descendants

    Gets descendants of client.
    
    Returns descendants.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    FoscInspection(a).descendants.do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    descendants {
        var descendants, localDescendants;
        descendants = [];

        if (client.isKindOf(FoscComponent)) { ^FoscDescendants(client) };
        assert(client.isKindOf(FoscSelection));
        
        client.do { |object|
            localDescendants = FoscInspection(object).descendants;
            localDescendants.do { |descendant|
                if (descendants.includes(descendant).not) {
                    descendants = descendants.add(descendant);
                };

            };
        };

        ^FoscSelection(descendants);
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration of client.
    
    Returns duration.

    def get_duration(
        self,
        in_seconds=False,
        ):
        return self._client._get_duration(
            in_seconds=in_seconds,
            )
    -------------------------------------------------------------------------------------------------------- */
    duration { |inSeconds=false|
        ^client.prGetDuration(inSeconds: inSeconds);
    }
    /* --------------------------------------------------------------------------------------------------------
    • effectiveIndicator

    Gets effective indicator that matches type and governs client.

    Returns indicator or none.
    
    **Example.** Gets components' effective clef:
    
    staff = Staff("c'4 d' e' f'")
    attach(Clef('alto'), staff)
    grace_container = scoretools.GraceContainer(
        [Note("fs'16")],
        kind='acciaccatura',
    )
    attach(grace_container, staff[-1])
    show(staff) # doctest: +SKIP
    
    f(staff)
    \new Staff {
        \clef "alto"
        c'4
        d'4
        e'4
        \acciaccatura {
            fs'16
        }
        f'4
    }
    
    for leaf in iterate(staff).by_class(with_grace_notes=True):
        clef = inspect_(leaf).get_effective(Clef)
        print(leaf, clef)

    Staff("c'4 d'4 e'4 f'4") Clef(name='alto')
    c'4 Clef(name='alto')
    d'4 Clef(name='alto')
    e'4 Clef(name='alto')
    fs'16 Clef(name='alto')
    f'4 Clef(name='alto')
    

    def get_effective(
        self,
        type=None,
        unwrap=True,
        n=0,
        ):
        return self._client._get_effective(
            type=type,
            unwrap=unwrap,
            n=n,
            )
    -------------------------------------------------------------------------------------------------------- */
    effectiveIndicator { |type, unwrap=true, n=0|
        //^client.prGetEffectiveIndicatorOfType(type: type, unwrap: unwrap, n: n);
        ^client.prGetEffective(type, unwrap, n);
    }
    /* --------------------------------------------------------------------------------------------------------
    • effective
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    effective { |type, attributes, default, n=0, unwrap=true|
        // if not isinstance(self.client, Component):
        //     raise Exception('can only get effective on components.')
        // if attributes is not None:
        //     assert isinstance(attributes, dict), repr(attributes)
        // result = self.client._get_effective(
        //     type,
        //     attributes=attributes,
        //     n=n,
        //     unwrap=unwrap,
        //     )
        // if result is None:
        //     result = default
        // return result
        var result;
        if (client.isKindOf(FoscComponent).not) {
            ^throw("%:%: can only get effective on a FoscComponent: %."
                .format(this.species, thisMethod.name, client));
        };
        if (attributes.notNil) { assert(attributes.isKindOf(Dictionary)) };
        result = client.prGetEffective(type, attributes: attributes, n: n, unwrap: unwrap);
        if (result.isNil) { result = default };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • effectiveStaff

    Gets effective staff of client.
    
    Returns staff or none.
    
    def get_effective_staff(self):
        return self._client._get_effective_staff()
    -------------------------------------------------------------------------------------------------------- */
    effectiveStaff {
        ^client.prGetEffectiveStaff;
    }
    /* --------------------------------------------------------------------------------------------------------
    • effectiveWrapper
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    effectiveWrapper { |type, attributes, n=0|
        if (attributes.notNil) { assert(attributes.isKindOf(Dictionary)) };
        ^this.effective(type, attributes: attributes, n: n, unwrap: false);
    }
    /* --------------------------------------------------------------------------------------------------------
    • graceContainer

    abjad 2.21

    Gets grace container attached to leaf.

    Returns grace container, acciaccatura container, appoggiatura container or none.
    
    def get_grace_container(self):
        return self._client._grace_container
    -------------------------------------------------------------------------------------------------------- */
    graceContainer {
        ^this.client.graceContainer;
    }
    /* --------------------------------------------------------------------------------------------------------
    • graceNote

    Is true when client is grace note.
    -------------------------------------------------------------------------------------------------------- */
    graceNote {
        var type;
        if (client.isKindOf(FoscLeaf).not) { ^false };
        type = [FoscAfterGraceContainer, FoscGraceContainer];
        client.prGetParentage.do { |component|
            if (type.any { |type| component.isKindOf(type) }) { ^true };
        };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • indicatorOfType (abjad: get_indicator)

    Gets indicator of type attached to client.
    
    Raises exception when more than one indicator of type attach to client.

    Returns default when no indicator of type attaches to client.

    Returns indicator or default.


    def get_indicator(
        self,
        type=None,
        default=None,
        unwrap=True,
        ):
        indicators = self._client._get_indicators(
                type=type,
                unwrap=unwrap,
                )
        if not indicators:
            return default
        elif len(indicators) == 1:
            return list(indicators)[0]
        else:
            message = 'multiple indicators attached to client.'
            raise Exception(message)
    -------------------------------------------------------------------------------------------------------- */
    indicatorOfType { |type, default, unwrap=true|
        var indicators;
        indicators = client.prGetIndicators(type: type, unwrap: unwrap);
        if (indicators.isEmpty) { ^default };
        if (indicators.size == 1) {
            ^indicators.as(Array)[0];
        } {
            ^throw("%:%: multiple indicators attached to client.".format(this.species, thisMethod.name));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • indicators (abjad: get_indicators)
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    indicators { |type, unwrap=true|
        if (client.isKindOf(FoscComponent).not) {
            ^throw(
                "%:%: can only get indicators on a FoscComponent: %."
                    .format(this.species, thisMethod.name, client);
            );
        };
        ^client.prGetIndicators(type: type, unwrap: unwrap);
    }
    /* --------------------------------------------------------------------------------------------------------
    • leafAt

    Gets leaf n.

    Returns leaf or none.

    
    Example score:

    ::

        >>> staff = Staff()
        >>> staff.append(Voice("c'8 d'8 e'8 f'8"))
        >>> staff.append(Voice("g'8 a'8 b'8 c''8"))
        >>> show(staff) # doctest: +SKIP

    ..  doctest::

        >>> f(staff)
        \new Staff {
            \new Voice {
                c'8
                d'8
                e'8
                f'8
            }
            \new Voice {
                g'8
                a'8
                b'8
                c''8
            }
        }

    

    **Example 1.** Gets leaf n **from** client of inspection agent
    when client of inspection agent is a leaf.

    With positive indices:

        >>> first_leaf = staff[0][0]
        >>> first_leaf
        Note("c'8")

        >>> for n in range(8):
        ...     print(n, inspect_(first_leaf).get_leaf(n))
        ...
        0 c'8
        1 d'8
        2 e'8
        3 f'8
        4 None
        5 None
        6 None
        7 None

    With negative indices:

        >>> last_leaf = staff[0][-1]
        >>> last_leaf
        Note("f'8")

        >>> for n in range(0, -8, -1):
        ...     print(n, inspect_(last_leaf).get_leaf(n))
        ...
        0 f'8
        -1 e'8
        -2 d'8
        -3 c'8
        -4 None
        -5 None
        -6 None
        -7 None

    
    **Example 2.** Gets leaf n **in** client of inspection agent when client of inspection agent is a container.

    With positive indices:

        >>> first_voice = staff[0]
        >>> first_voice
        Voice("c'8 d'8 e'8 f'8")

        >>> for n in range(8):
        ...     print(n, inspect_(first_voice).get_leaf(n))
        ...
        0 c'8
        1 d'8
        2 e'8
        3 f'8
        4 None
        5 None
        6 None
        7 None

    With negative indices:

        >>> first_voice = staff[0]
        >>> first_voice
        Voice("c'8 d'8 e'8 f'8")


        >>> for n in range(-1, -9, -1):
        ...     print(n, inspect_(first_voice).get_leaf(n))
        ...
        -1 f'8
        -2 e'8
        -3 d'8
        -4 c'8
        -5 None
        -6 None
        -7 None
        -8 None
    

    def get_leaf(self, n=0):
        from abjad.tools import scoretools
        if isinstance(self._client, scoretools.Leaf):
            return self._client._get_leaf(n=n)
        if 0 <= n:
            leaves = iterate(self._client).by_leaf(start=0, stop=n+1)
            leaves = list(leaves)
            if len(leaves) < n + 1:
                return
            leaf = leaves[n]
            return leaf
        else:
            leaves = iterate(self._client).by_leaf(
                start=0,
                stop=abs(n),
                reverse=True,
                )
            leaves = list(leaves)
            if len(leaves) < abs(n):
                return
            leaf = leaves[abs(n)-1]
            return leaf
    
    a = FoscStaff([
        FoscVoice({ |i| FoscNote(0 + i, [1, 4]) } ! 10),
        FoscVoice({ |i| FoscNote(10 + i, [1, 4]) } ! 10)
    ]);
    FoscInspection(a).leafAt(7).pitch.pitchNumber;
    FoscInspection(a.select.leaves[5]).leafAt(2).pitch.pitchNumber;
    FoscInspection(a.select.leaves[4]).leafAt(-3).pitch.pitchNumber;
    -------------------------------------------------------------------------------------------------------- */
    leafAt { |n=0|
        var leaves, leaf;
        if (client.isKindOf(FoscLeaf)) { ^client.prLeafAt(n) };
        if (n >= 0) {
            leaves = all(FoscIteration(client).leaves);
            if (n >= leaves.size) { ^nil };
            leaf = leaves[n];
            ^leaf;
        } {
            leaves = all(FoscIteration(client).leaves);
            leaves = leaves[0..(n.abs - 1)].reverse;
            if (n.abs >= leaves.size) { ^nil };
            leaf = leaves[n.abs - 1];
            ^leaf;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • lineage

    Gets lineage of client.
    
    Returns lineage.

    def get_lineage(self):
        return self._client._get_lineage()
    -------------------------------------------------------------------------------------------------------- */
    lineage {
        ^client.prLineage;
    }
    /* --------------------------------------------------------------------------------------------------------
    • logicalTie

    Gets logical tie that governs leaf.
    
    Returns logical tie.

    def get_logical_tie(self):
        return self._client._get_logical_tie()

    a = [FoscNote(60, 1), FoscNote(60, 2)];
    FoscAttach(FoscTie(), a);
    FoscInspection(a[0]).logicalTie.music;
    -------------------------------------------------------------------------------------------------------- */
    logicalTie {
        ^client.prGetLogicalTie;
    }
    /* --------------------------------------------------------------------------------------------------------
    • markup

    Gets all markup attached to client.
    
    Returns tuple.

    def get_markup(
        self,
        direction=None,
        ):
        return self._client._get_markup(
            direction=direction,
            )
    -------------------------------------------------------------------------------------------------------- */
    markup { |direction|
        ^client.prGetMarkup(direction: direction);
    }
    /* --------------------------------------------------------------------------------------------------------
    • parentage

    Gets parentage of client.

    Returns parentage.
    
    **Example 1.** Gets parentage without grace notes:


        >>> voice = Voice("c'4 d'4 e'4 f'4")
        >>> grace_notes = [Note("c'16"), Note("d'16")]
        >>> grace_container = scoretools.GraceContainer(
        ...     grace_notes,
        ...     kind='grace',
        ...     )
        >>> attach(grace_container, voice[1])
        >>> show(voice) # doctest: +SKIP

    ..  doctest::

        >>> f(voice)
        \new Voice {
            c'4
            \grace {
                c'16
                d'16
            }
            d'4
            e'4
            f'4
        }


        >>> inspect_(grace_notes[0]).get_parentage()
        Parentage([Note("c'16"), GraceContainer("c'16 d'16")])


    **Example 2.** Gets parentage with grace notes:


        >>> voice = Voice("c'4 d'4 e'4 f'4")
        >>> grace_notes = [Note("c'16"), Note("d'16")]
        >>> grace_container = scoretools.GraceContainer(
        ...     grace_notes,
        ...     kind='grace',
        ...     )
        >>> attach(grace_container, voice[1])
        >>> show(voice) # doctest: +SKIP

    ..  doctest::

        >>> f(voice)
        \new Voice {
            c'4
            \grace {
                c'16
                d'16
            }
            d'4
            e'4
            f'4
        }

    ::

        >>> inspector = inspect_(grace_notes[0])
        >>> inspector.get_parentage(with_grace_notes=True)
        Parentage([Note("c'16"), GraceContainer("c'16 d'16"), Note("d'4"), Voice("c'4 d'4 e'4 f'4")])
    

    def get_parentage(
        self,
        include_self=True,
        with_grace_notes=False,
        ):
        return self._client._get_parentage(
            include_self=include_self,
            with_grace_notes=with_grace_notes,
            )
    
    a = FoscNote(60, 1);
    FoscVoice([a]);
    FoscInspection(a).parentage.music;
    -------------------------------------------------------------------------------------------------------- */
    parentage { |includeSelf=true, graceNotes=false|
        ^client.prGetParentage(includeSelf, graceNotes);
    }
    /* --------------------------------------------------------------------------------------------------------
    • piecewise

    Gets piecewise indicators attached to client.

    Returns indicator or default.

    def get_piecewise(self, type=None, default=None):
        wrappers = self.get_indicators(type=type, unwrap=False)
        wrappers = wrappers or []
        wrappers = [_ for _ in wrappers if _.is_piecewise]
        if not wrappers:
            return default
        if len(wrappers) == 1:
            return wrappers[0].indicator
        if 1 < len(wrappers):
            message = 'multiple indicators attached to client.'
            raise Exception(message)
    -------------------------------------------------------------------------------------------------------- */
    piecewise { |type, default|
        var wrappers;
        wrappers = this.indicators(type: type, unwrap: false);
        wrappers = wrappers ? [];
        wrappers = wrappers.select { |each| each.isPiecewise };
        if (wrappers.isEmpty) { ^default };
        if (wrappers.size == 1) { ^wrappers[0].indicator };
        if (wrappers.size > 1) {
            ^throw("%:%: multiple indicators attached to client.".format(this.species, thisMethod.name));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    •

    Gets sounding pitch of client.

    Returns pitch.
    

    >>> staff = Staff("d''8 e''8 f''8 g''8")
    >>> piccolo = instrumenttools.Piccolo()
    >>> attach(piccolo, staff)
    >>> instrumenttools.transpose_from_sounding_pitch_to_written_pitch(
    ...     staff)
    >>> show(staff) # doctest: +SKIP

..  doctest::

    >>> f(staff)
    \new Staff {
        \set Staff.instrumentName = \markup { Piccolo }
        \set Staff.shortInstrumentName = \markup { Picc. }
        d'8
        e'8
        f'8
        g'8
    }
    >>> inspect_(staff[0]).get_sounding_pitch()
    NamedPitch("d''")
    


    def get_sounding_pitch(self):
        return self._client._get_sounding_pitch()
    -------------------------------------------------------------------------------------------------------- */
    soundingPitch {
        ^client.prSoundingPitch;
    }
    /* --------------------------------------------------------------------------------------------------------
    •

    Gets sounding pitches of client.

    Returns tuple.
    

    >>> staff = Staff("<c''' e'''>4 <d''' fs'''>4")
    >>> glockenspiel = instrumenttools.Glockenspiel()
    >>> attach(glockenspiel, staff)
    >>> instrumenttools.transpose_from_sounding_pitch_to_written_pitch(
    ...     staff)
    >>> show(staff) # doctest: +SKIP


    >>> f(staff)
    \new Staff {
        \set Staff.instrumentName = \markup { Glockenspiel }
        \set Staff.shortInstrumentName = \markup { Gkspl. }
        <c' e'>4
        <d' fs'>4
    }

    >>> inspect_(staff[0]).get_sounding_pitches()
    (NamedPitch("c'''"), NamedPitch("e'''"))
    

    def get_sounding_pitches(self):
        return self._client._get_sounding_pitches()
    -------------------------------------------------------------------------------------------------------- */
    soundingPitches {
        ^client.prSoundingPitches;
    }
    /* --------------------------------------------------------------------------------------------------------
    • spannerOfType

    Gets spanner of type attached to client.
    
    Raises exception when more than one spanner of type attaches to client.

    Returns default when no spanner of type attaches to client.

    Returns spanner or default.

    def get_spanner(
        self,
        type=None,
        default=None,
        in_parentage=False,
        ):
        spanners = self._client._get_spanners(
            type=type,
            in_parentage=in_parentage,
            )
        if not spanners:
            return default
        elif len(spanners) == 1:
            return list(spanners)[0]
        else:
            message = 'multiple spanners attached to client.'
            raise Exception(message)
    
    a = [FoscNote(60, 1), FoscNote(60, 2)];
    FoscAttach(FoscTie(), a);
    FoscInspection(a[0]).spannerOfType(FoscTie);
    -------------------------------------------------------------------------------------------------------- */
    spannerOfType { |type, default, inParentage=false|
        var spanners;
        spanners = client.prSpanners(type, inParentage);
        if (spanners.isEmpty) {
            ^default;
        } {
            if (spanners.size == 1) { ^spanners.as(Array)[0] };
        };
        ^throw("%:%: multiple spanners attached to client.".format(this.species, thisMethod.name));
    }   
    /* --------------------------------------------------------------------------------------------------------
    • spanners

    Gets spanners attached to client.
    
    Returns set.

    def get_spanners(
        self,
        type=None,
        in_parentage=False,
        ):
        return self._client._get_spanners(
            type=type,
            in_parentage=in_parentage,
            )
    
    a = [FoscNote(60, 1), FoscNote(60, 2)];
    FoscAttach(FoscTie(), a);
    FoscInspection(a[0]).spanners;
    -------------------------------------------------------------------------------------------------------- */
    spanners { |type, inParentage=false|
        ^client.prSpanners(type, inParentage);
    }
    /* --------------------------------------------------------------------------------------------------------
    • timespan

    Gets timespan of client.

    Returns timespan.
    
    **Example.** Gets timespan of grace notes:

    ::

        >>> voice = Voice("c'8 [ d'8 e'8 f'8 ]")
        >>> grace_notes = [Note("c'16"), Note("d'16")]
        >>> grace = scoretools.GraceContainer(
        ...     grace_notes,
        ...     kind='grace',
        ...     )
        >>> attach(grace, voice[1])
        >>> after_grace_notes = [Note("e'16"), Note("f'16")]
        >>> after_grace = scoretools.GraceContainer(
        ...     after_grace_notes,
        ...     kind='after')
        >>> attach(after_grace, voice[1])
        >>> show(voice) # doctest: +SKIP

    ..  doctest::

        >>> f(voice)
        \new Voice {
            c'8 [
            \grace {
                c'16
                d'16
            }
            \afterGrace
            d'8
            {
                e'16
                f'16
            }
            e'8
            f'8 ]
        }

    ::

        >>> for leaf in iterate(voice).by_leaf(with_grace_notes=True):
        ...     timespan = inspect_(leaf).get_timespan()
        ...     print(str(leaf) + ':')
        ...     print(format(timespan, 'storage'))
        c'8:
        timespantools.Timespan(
            start_offset=durationtools.Offset(0, 1),
            stop_offset=durationtools.Offset(1, 8),
            )
        c'16:
        timespantools.Timespan(
            start_offset=durationtools.Offset(
                (1, 8),
                grace_displacement=durationtools.Duration(-1, 8),
                ),
            stop_offset=durationtools.Offset(
                (1, 8),
                grace_displacement=durationtools.Duration(-1, 16),
                ),
            )
        d'16:
        timespantools.Timespan(
            start_offset=durationtools.Offset(
                (1, 8),
                grace_displacement=durationtools.Duration(-1, 16),
                ),
            stop_offset=durationtools.Offset(1, 8),
            )
        d'8:
        timespantools.Timespan(
            start_offset=durationtools.Offset(1, 8),
            stop_offset=durationtools.Offset(1, 4),
            )
        e'16:
        timespantools.Timespan(
            start_offset=durationtools.Offset(
                (1, 4),
                grace_displacement=durationtools.Duration(-1, 8),
                ),
            stop_offset=durationtools.Offset(
                (1, 4),
                grace_displacement=durationtools.Duration(-1, 16),
                ),
            )
        f'16:
        timespantools.Timespan(
            start_offset=durationtools.Offset(
                (1, 4),
                grace_displacement=durationtools.Duration(-1, 16),
                ),
            stop_offset=durationtools.Offset(1, 4),
            )
        e'8:
        timespantools.Timespan(
            start_offset=durationtools.Offset(1, 4),
            stop_offset=durationtools.Offset(3, 8),
            )
        f'8:
        timespantools.Timespan(
            start_offset=durationtools.Offset(3, 8),
            stop_offset=durationtools.Offset(1, 2),
            )
    

    def get_timespan(self, in_seconds=False):
        return self._client._get_timespan(
            in_seconds=in_seconds,
            )
    -------------------------------------------------------------------------------------------------------- */
    timespan { |inSeconds=false|
        ^client.prGetTimespan(inSeconds: inSeconds);
    }
    /* --------------------------------------------------------------------------------------------------------
    • verticalMoment

    Gets vertical moment starting with client.
    
    Returns vertical moment.
    
    def get_vertical_moment(
        self,
        governor=None,
        ):
        return self._client._get_vertical_moment(
            governor=governor,
            )
    -------------------------------------------------------------------------------------------------------- */
    verticalMoment { |governor|
        ^client.prVerticalMoment(governor: governor);
    }
    /* --------------------------------------------------------------------------------------------------------
    • verticalMomentAt

    Gets vertical moment at offset.
    
    Returns vertical moment.

    def get_vertical_moment_at(
        self,
        offset,
        ):
        return self._client._get_vertical_moment_at(
            offset,
            )
    -------------------------------------------------------------------------------------------------------- */
    verticalMomentAt { |offset|
        ^client.prVerticalMomentAt(offset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasEffectiveIndicator

    Is true when indicator that matches type is in effect for client. Otherwise false.

    Returns true or false.

    def has_effective_indicator(self, type=None):
        return self._client._has_effective_indicator(type=type)
    -------------------------------------------------------------------------------------------------------- */
    hasEffectiveIndicator { |type|
        ^client.prHasEffectiveIndicator(type: type);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasIndicator
    
    Is true when client has one or more indicators that match type. Otherwise false.
    
    Returns true or false.

    def has_indicator(self, type=None):
        return self._client._has_indicator(type=type)
    -------------------------------------------------------------------------------------------------------- */
    hasIndicator { |type|
        ^client.prHasIndicator(type: type);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasSpanner

    Is true when client has one or more spanners that match type. Otherwise false.
    
    Returns true or false.

    def has_spanner(
        self,
        type=None,
        in_parentage=False,
        ):
        return self._client._has_spanner(
            type=type,
            in_parentage=in_parentage,
            )
    -------------------------------------------------------------------------------------------------------- */
    hasSpanner { |type, inParentage=false|
        ^client.prHasSpanner(type, inParentage);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isBarLineCrossing

    Is true when client crosses bar line. Otherwise false.

    Returns true or false.


    >>> staff = Staff("c'4 d'4 e'4")
    >>> time_signature = TimeSignature((3, 8))
    >>> attach(time_signature, staff)
    >>> show(staff) # doctest: +SKIP

..  doctest::

    >>> f(staff)
    \new Staff {
        \time 3/8
        c'4
        d'4
        e'4
    }

::

    >>> for note in staff:
    ...     result = inspect_(note).is_bar_line_crossing()
    ...     print(note, result)
    ...
    c'4 False
    d'4 True
    e'4 False


    def is_bar_line_crossing(self):
        from abjad.tools import indicatortools
        time_signature = self._client._get_effective(
            indicatortools.TimeSignature)
        if time_signature is None:
            time_signature_duration = durationtools.Duration(4, 4)
        else:
            time_signature_duration = time_signature.duration
        partial = getattr(time_signature, 'partial', 0)
        partial = partial or 0
        start_offset = self._client._get_timespan().start_offset
        shifted_start = start_offset - partial
        shifted_start %= time_signature_duration
        stop_offset = self._client._get_duration() + shifted_start
        if time_signature_duration < stop_offset:
            return True
        return False
    -------------------------------------------------------------------------------------------------------- */
    isBarLineCrossing {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isWellFormed

    Is true when client is well-formed. Otherwise false.
    
    Returns false.

    def is_well_formed(self):
        from abjad.tools import systemtools
        manager = systemtools.WellformednessManager()
        for violators, total, check_name in manager(self._client):
            if violators:
                return False
        return True
    -------------------------------------------------------------------------------------------------------- */
    isWellFormed {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • reportModifications

    Reports modifications of client. Returns string.
    

    Report modifications of container in selection:

    >>> container = Container("c'8 d'8 e'8 f'8")
    >>> override(container).note_head.color = 'red'
    >>> override(container).note_head.style = 'harmonic'
    >>> show(container) # doctest: +SKIP


    >>> f(container)
    {
        \override NoteHead.color = #red
        \override NoteHead.style = #'harmonic
        c'8
        d'8
        e'8
        f'8
        \revert NoteHead.color
        \revert NoteHead.style
    }


    >>> report = inspect_(container).report_modifications()

    >>> print(report)
    {
        \override NoteHead.color = #red
        \override NoteHead.style = #'harmonic
        %%% 4 components omitted %%%
        \revert NoteHead.color
        \revert NoteHead.style
    }

            

    def report_modifications(self):
        from abjad.tools import scoretools
        from abjad.tools import systemtools
        client = self._client
        bundle = systemtools.LilyPondFormatManager.bundle_format_contributions(
            client)
        result = []
        result.extend(client._get_format_contributions_for_slot(
            'before', bundle))
        result.extend(client._get_format_contributions_for_slot(
            'open brackets', bundle))
        result.extend(client._get_format_contributions_for_slot(
            'opening', bundle))
        result.append('    %%%%%% %s components omitted %%%%%%' % len(client))
        result.extend(client._get_format_contributions_for_slot(
            'closing', bundle))
        result.extend(client._get_format_contributions_for_slot(
            'close brackets', bundle))
        result.extend(client._get_format_contributions_for_slot(
            'after', bundle))
        result = '\n'.join(result)
        return result
    -------------------------------------------------------------------------------------------------------- */
    reportModifications {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • tabulateWellFormednessViolations

    Tabulates well-formedness violations in client.

    Returns string.
    

    >>> staff = Staff("c'8 d'8 e'8 f'8")
    >>> staff[1].written_duration = Duration(1, 4)
    >>> beam = spannertools.Beam()
    >>> attach(beam, staff[:])

    >>> f(staff)
    \new Staff {
        c'8 [
        d'4
        e'8
        f'8 ]
    }


    >>> result = inspect_(staff).tabulate_well_formedness_violations()


    >>> print(result)
    1 / 4 beamed quarter notes
    0 / 1 conflicting clefs
    0 / 1 discontiguous spanners
    0 / 5 duplicate ids
    0 / 1 empty containers
    0 / 0 intermarked hairpins
    0 / 0 misdurated measures
    0 / 0 misfilled measures
    0 / 0 mismatched enchained hairpins
    0 / 0 mispitched ties
    0 / 4 misrepresented flags
    0 / 5 missing parents
    0 / 0 nested measures
    0 / 1 overlapping beams
    0 / 0 overlapping glissandi
    0 / 0 overlapping hairpins
    0 / 0 overlapping octavation spanners
    0 / 0 overlapping ties
    0 / 0 short hairpins
    0 / 0 tied rests

    Beamed quarter notes are not well formed.
    

    def tabulate_well_formedness_violations(self):
        from abjad.tools import systemtools
        manager = systemtools.WellformednessManager()
        triples = manager(self._client)
        strings = []
        for violators, total, check_name in triples:
            violator_count = len(violators)
            string = '{} /\t{} {}'
            check_name = check_name.replace('check_', '')
            check_name = check_name.replace('_', ' ')
            string = string.format(violator_count, total, check_name)
            strings.append(string)
        return '\n'.join(strings)
    -------------------------------------------------------------------------------------------------------- */
    tabulateWellFormednessViolations {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • wrappers
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    wrappers { |type|
        ^this.indicators(type, false);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • client
    
    Client of inspection agent.
    
    Returns component.

    a = FoscInspection(FoscNote(60, 1));
    a.client;

    @property
    def client(self):
        return self._client
    -------------------------------------------------------------------------------------------------------- */
}
