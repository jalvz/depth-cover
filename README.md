depth-cover
===========

Fast Depth of Coverage Analyser (for BAM, BED files)



Installation and execution:

    Download the file depth-cover.jar and execute with java:
    
    java -XmxMAX_MEMORY_ALLOCATION -XX:+UseParallelGC -jar depth-cover.jar -bam BAM_FILE [OPTIONS]
    
    The options are explained in the help. To see the help, just type:
    
    java -jar depth-cover.jar



Usage:

    depth-cover consumes files in BAM format. Optionally, it accepts a BED file for interval calculations
    and a FASTA file to remove the Ns in the reference from calculation.
    
    depth-cover produces 4 output files:
    
    *.summary.csv contains the number of reads and the mean coverage per genome and chromosomes / intervals.
    
    *.coverage.csv contains the coverage per genome / intervals.
    
    *.breakdown.csv contains the coverage per chromosome / interval.
    
    *.details.csv contains the coverage per locus (optional, not provided by default).



Performance and requirements:

    depth-cover is several orders of magnitude faster than other tools. Execution time and memory consumption
    depends on the size of the BAM file.
    
    Recommended memory allocation is 20% - 50% of the size of the BAM file - i.e.: if your BAM is 10Gb,
    execute like java -Xmx2g -XX:+UseParallelGC -jar depth-cover.jar ...
    
    The JVM option -XX:+UseParallelGC is not mandatory, but usually is a good idea (particularly, if memory
    is a scarce resource).
    
    If the BAM file is bigger than 4 Gb, it is indexed and there is enough memory, depth-cover will read the 
    chromosomes in parallel, performing 50% faster in some cases. 
    
    The parallel reader makes intensive use of CPU and RAM. 
    If you use a shared computer, you might want to disable it with the --ignore-index option.
    


Note for developers:

    Not all the resources for unit tests are included. This means that if you try to run 'mvn clean install' it will fail. 
    The missing resources are available on request.
