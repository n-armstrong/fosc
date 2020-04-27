/* -----------------------------------------------====---------------------------------------------------------
• FoscEvent

Abstract base class from which concrete FoscEvent subclasses inherit.

Represents an attack point to be quantized.

All FoscEvents possess a rational offset in seconds, and an optional index for disambiguating events which fall on the same offset in a FoscQGrid.
------------------------------------------------------------------------------------------------------------ */
FoscEvent : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <offset, <attachments, <index;
    *new { |offset=0, attachments, index|
        ^super.new.init(offset, attachments, index);
    }
    init { |argOffset, argAttachments, argIndex|
        offset = FoscOffset(argOffset);
        attachments = argAttachments ?? { List[] };
        attachments = attachments.as(List);
        index = argIndex;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when `expr` is a q-event with offset greater than that of this q-event. Otherwise false.
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
        if (this.species != expr.species) { ^false };
        if (offset < expr.offset) { ^true };
        ^false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    def _get_format_specification(self):
        agent = systemtools.StorageFormatAgent(self)
        names = agent.signature_keyword_names
        for name in ('attachments',):
            if not getattr(self, name, None) and name in names:
                names.remove(name)
        return systemtools.FormatSpecification(
            client=self,
            repr_is_indented=False,
            storage_format_kwargs_names=names,
            )
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatSpecification {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • index

    The optional index, for sorting Events with identical offsets.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • offset
    
    The offset in milliseconds of the event.
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
    }
}