#!/bin/bash
out=$(ls -d */ | sed 's/\t/\n/g' | grep -v dist)
if [ -d dist ]; then
	echo "Start building"
else
	echo "Creating dist"
	mkdir dist
fi
for folder in $out; do
	echo "Building ${folder}..."
	cd $folder
	if [ -e pom.xml ]; then
		mvn install
		if [ $? -ne 0 ]; then
			echo "FATAL ERROR OCCURED, abort"
			exit 1
		fi
		cp target/*.jar ../dist/
		echo "Built ${folder}"
		cd ..
	fi
done

