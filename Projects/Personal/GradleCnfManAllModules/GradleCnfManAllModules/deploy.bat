@echo off

pushd ..\..\..\..\Projects\Personal\GradleCnfMan\GradleCnfMan
call gradlew fatJar sourcesJar --console=plain
popd
