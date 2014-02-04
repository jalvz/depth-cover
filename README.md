depth-cover
===========

Fast Depth of Coverage Analyser (for BAM, BED files)



**Installation and execution:**

    Download the program ('Download ZIP button') and unzip it. 
    To run the program only the file depth-cover.jar is needed.
    Execute depth-cover with:
    
    java -XmxMAX_MEMORY_ALLOCATION -XX:+UseParallelGC -jar depth-cover.jar -bam BAM_FILE [OPTIONS]

    Options are detailed in the help. To see the help type:
    
    java -jar depth-cover.jar



**Usage:**

    depth-cover consumes files in BAM format. By default it calculates the coverage of the genome used
    for the alignemnt. Optionally, it accepts a BED file to calculate the coverage in specific interval(s)
    [-bed option]. To calculate the coverage without taking into account the Ns in the reference a FASTA 
    file can be provided [-fasta option].
    
    
    depth-cover produces 4 output files:
    
    *.summary.csv - number of reads and the mean coverage per genome and chromosomes / intervals.
    
    *.coverage.csv - coverage per genome / intervals.
    
    *.breakdown.csv - coverage per chromosome / interval.
    
    *.details.csv - coverage per locus (optional).



**Performance and requirements:**

    depth-cover is several orders of magnitude faster than other tools. Execution time and memory 
    consumption depend on the size of the BAM file. In a modern desktop computer, it can process 
    15-30 million reads per minute.
    
    Recommended memory allocation is 20% - 50% of the size of the BAM file - i.e.: if your BAM is 10Gb,
    execute like: 
    
    java -Xmx2g -XX:+UseParallelGC -jar depth-cover.jar ARGUMENTS
    
    The JVM option -XX:+UseParallelGC is not mandatory, but usually it is a good idea (particularly, if 
    memory is a scarce resource).
    
    If the BAM file is indexed and bigger than 4 Gb, and there is enough memory, depth-cover will read
    the chromosomes in parallel, performing up to 50% faster. 
    
    The parallel reader makes intensive use of CPU and RAM. 
    If you use a shared computer, you might want to disable it with the --ignore-index option.
    


**Troubleshooting and known issues:**
 
    If the parallel reader is enabled (see above) and one chromosome has zero reads, depth-cover will hang.
    The --ignore-index flag solves this issue.
    
    Processing intervals in a not indexed BAM file takes as much time as it would take for the whole genome.
    It is highly recommended to use indexed BAM files for interval processing.
    
    depth-cover uses samtools-1.52.944. Any bug in this library might affect depth-cover as well.



**Note for developers:**

    Not all the resources for unit tests are included. This means that if you try to run 'mvn install' 
    it will fail. The missing resources are available under request.
