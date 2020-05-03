# Fosc

## <br>Basic usage

### <br>1. Displaying music


<br>Iterate over all score components.

```supercollider
a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
a.show;
```
![](./images/test.png)


<br>Iterate over all score components.

```supercollider
a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
a.doComponents({ |each| each.cs.postln });
```

```
FoscStaff([  ], 'Staff', false)
FoscNote('C4', 1/8)
FoscNote('D4', 1/8)
FoscNote('E4', 1/8)
FoscNote('F4', 1/8)
FoscNote('G4', 1/8)
FoscNote('A4', 1/8)
FoscNote('B4', 1/8)
FoscNote('C5', 1/8)
```

## <br>Another section

### <br>1. Displaying music


<br>Iterate over all score components.

```supercollider
a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
a.show;
```
![](./images/test.png)


<br>Iterate over all score components.

```supercollider
a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
a.doComponents({ |each| each.cs.postln });
```

```
FoscStaff([  ], 'Staff', false)
FoscNote('C4', 1/8)
FoscNote('D4', 1/8)
FoscNote('E4', 1/8)
FoscNote('F4', 1/8)
FoscNote('G4', 1/8)
FoscNote('A4', 1/8)
FoscNote('B4', 1/8)
FoscNote('C5', 1/8)
```
