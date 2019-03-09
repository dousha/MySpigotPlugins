#!/bin/bash
out=$(ls -d */ | sed 's/\t/\n/g' | grep -v dist)
for folder in $out; do
	echo "Building ${folder}..."
	cd $folder
	if [ -e pom.xml ]; then
		mvn install
		cp target/*.jar ../dist/
		echo "Built ${folder}"
		cd ..
	fi
done

