@echo off
echo Wait... Runnnig R to calculate HSD Test
cd temp
rscript hsd_test.r > hsd_result.txt
rem pause
exit