/* --------------------------------------------------------------------------------------------------------
• FoscSilentEvent

A FoscEvent which indicates the onset of a period of silence in a FoscEventSequence.

q = FoscSilentEvent(1);
q.offset.pair;
q.attachments;
-------------------------------------------------------------------------------------------------------- */
FoscSilentEvent : FoscEvent {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <bundleActions;
    *new { |offset=0, attachments, index, bundleActions|
        ^super.new(offset, attachments, index).initFoscSilentEvent(bundleActions);
    }
    initFoscSilentEvent { |argBundleActions|
        bundleActions = argBundleActions;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
    def __eq__(self, expr):
            r'''Is true when `expr` is a silent q-event with offset, attachments and
            index equal to those of this silent q-event. Otherwise false.
    
            Returns true or false.
            '''
            if type(self) == type(expr) and \
                self._offset == expr._offset and \
                self._attachments == expr._attachments and \
                self._index == expr._index:
                return True
            return False
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        var bool;
        bool = ((this.species == expr.species)
            && { offset == expr.offset }
            && { attachments == expr.attachments }
            && { index == expr.index });
        ^bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def __hash__(self):
            r'''Hashes silent q-event.
    
            Required to be explicitly redefined on Python 3 if __eq__ changes.
    
            Returns integer.
            '''
            return super(SilentEvent, self).__hash__()
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def attachments(self):
            r'''Attachments of silen q-event.
            '''
            return self._attachments
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        Post << this << nl;
        if (index.notNil) { Post.tab << "index: " << index << nl };
        Post.tab << "offset: " << offset.asFloat << nl;
        if (attachments.notEmpty) { Post.tab << "attachments: " << attachments << nl };
    }
}
