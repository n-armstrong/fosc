# fosc

[lilypond]: http://lilypond.org/
[supercollider]: https://supercollider.github.io/

__fosc__ is a [SuperCollider][supercollider] API for generating musical notation in [LilyPond][lilypond].


## Installation

### 1. Install LilyPond

[LilyPond][lilypond] is an open-source program that engraves music notation in an automated way. Fosc uses LilyPond to produce notational output. It's recommended that you install the most recent version of LilyPond directly from the LilyPond website. After you install LilyPond, check to see if LilyPond is callable from your commandline.

    ~$ lilypond --version

    GNU LilyPond 2.19.83

    Copyright (c) 1996--2015 by
      Han-Wen Nienhuys <hanwen@xs4all.nl>
      Jan Nieuwenhuizen <janneke@gnu.org>
      and others.

    This program is free software.  It is covered by the GNU General Public
    License and you are welcome to change it and/or distribute copies of it
    under certain conditions.  Invoke as `lilypond --warranty` for more
    information.

If LilyPond is not callable from your commandline, you can follow the instructions provided at http://www.lilypond.org/macos-x.html. Alternatively, add the location of the LilyPond executable to your ``PATH`` environment variable. Under OSX you can update your path like this:

```bash
export PATH="$PATH:/Applications/LilyPond.app/Contents/Resources/bin/"
```

### 2. Install fosc

Install Abjad like this:

..  code-block:: bash

    ~$ pip install abjad

After installation, check that Python can import Abjad:

    ~$ python
    Python 3.7.4 (v3.7.4:e09359112e, Jul  8 2019, 14:54:52) 
    [Clang 6.0 (clang-600.0.57)] on darwin
    Type "help", "copyright", "credits" or "license" for more information.
    >>> import abjad
    >>> abjad.__version__
    '3.1'

Congratulations! Easy install is complete after you install LilyPond and Abjad. Skip section B of this document. Look over optional sections C, D and E. Then read through the tutorials and examples for ideas about where to go next.



### 3. Configure fosc

Abjad creates a ``~/.abjad`` directory the first time it runs. In the ``~/.abjad`` directory you will find an ``abjad.cfg`` file. This is the Abjad configuration file. You can use the Abjad configuration file to tell Abjad about your preferred PDF file viewer, MIDI player, LilyPond language and so on. Your configuration file will look something like this the first time you open it:

```supercollider
FoscConfiguration.lilypondExecutablePath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";
```
