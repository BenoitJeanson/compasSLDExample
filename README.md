# compasSLDExample

Quick and dirty PoC on rendering a substation described in a scd.

The various options are called this way :

`SLDGenerator.computeAndWriteResults(String fileName_prefix, boolean stacked, boolean semiAutomaticPlacement);`


## Full automatic placement (`PositionByClustering`)

`semiAutomaticPlacement = false;`

Results are found here [./results](results).
Output Files are json and svg.

`stacked = false`

![./results/test_unstacked.svg](results/test_unstacked.svg)

`stacked = true`

![./results/test_stacked.svg](results/test_stacked.svg)

## Semi automatic placement (`PositionFromExtension`)

`semiAutomaticPlacement = true;`

The algorithm requires all the information  for all the BusBars and Feeders or will crash.

When a bay defines a BusBar :
- `sxy:y` is used to define the `BusBarIndex` in `scdGraphBuilder::createBusBarSection`
- `syx:x` is used to define the `BusBarSection` in `scdGraphBuilder::createBusBarSection`

And for the Feeder `scdGraphBuilder::createLoad`:
- `sxy:x` of the parent `Bay` is used for the `order` of the feeder.
- no usefull information is found for the direction, therefore set to arbitrary value `Direction.TOP`
- To enable the coupling to be in the same area as the grounded feeder (same bay), `sxy:x` is put as the order of any breaker found. The information is not used for `ExternCell`, but when is used to define the order of an `InternCell`


with the stack option = true
![./results/test_ExplicitPosition_stacked.svg](results/test_ExplicitPosition_stacked.svg)

## Road Map
- Done: parsing to create a corresponding graph.
- Done: add new kinds of component - that are beyond the one that are use for operation
- Done: there are disconnectors that do have only one connecting node. We will need to add fictitious feederNode to display the outgoing connected element (line / TR /....), but ideally, having - the information would be better.
- Done: there are connections to the ground. For this PoC, this will be a kind of feeder... but this is something that you may want to improve.
- Done: there are also informations regarding x,y position, that could be used for the semi-automatic algorithm. I will propose both approaches.
- Done: render!
- Todo: then we will have to handle more complex structures: the one proposed in the sample is simple... but let's keep that for a next step.
