package generator;

public class BashRunnerGenerator {
	private static write(outputDirectory, projectName) {
		def runMultiBashFile = new File("$outputDirectory/run.sh")
		
		runMultiBashFile.write("""#!/bin/bash

# Generated by BashRunnerGenerator

declare -r script_dir=`cd "\$( dirname "\$0" )" && pwd`
cd \$script_dir

processes_count=\$1

if [ -z "\$processes_count" ]; then
	processes_count="1"
fi

interval_seconds=\$2

if [ -z "\$interval_seconds" ]; then
	interval_seconds="1"
fi

host_name=`hostname`

for ((i=1;i<=\$processes_count;i++)); do
	deploymentName=\$((\$i%5))
	deploymentName="$projectName-\$deploymentName-\$host_name"
	
	echo "Running agent number \$i"
	date
	echo java -Dtakipi.name=$projectName -Dtakipi.deployment.name="\$deploymentName" -Xmx10m -Xms10m -cp \$script_dir/build/libs/${projectName}.jar helpers.Main \
			-ec 1440 -im 60000 -rc 365 -wm 0 -st -hs -sp -fc 10 -aa "$projectName-\$deploymentName"
	echo ""
	
	nohup java -Dtakipi.name=$projectName -Dtakipi.deployment.name="\$deploymentName" -Xmx10m -Xms10m -cp \$script_dir/build/libs/${projectName}.jar helpers.Main \
			-ec 1440 -im 60000 -rc 365 -wm 0 -st -hs -sp -fc 10 -aa "\$deploymentName" &
			
	sleep \$interval_seconds
done

""")
		
		Utils.ant.chmod(file:"$runMultiBashFile", perm:"+x")
	}
}