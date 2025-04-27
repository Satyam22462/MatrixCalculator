cmake_minimum_required(VERSION 3.22.1)

project("matrixcalculator20")

# Tell CMake to include the Eigen header directory
include_directories(include)

add_library(
        native-lib
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${log-lib})
