#!/usr/bin/env python3
import json
import sys
from pathlib import Path

if len(sys.argv) != 3:
    print("Usage: check_perf_regressions.py <benchmark_json> <thresholds_json>")
    sys.exit(2)

benchmark_path = Path(sys.argv[1])
thresholds_path = Path(sys.argv[2])

if not benchmark_path.exists():
    print(f"Benchmark report not found: {benchmark_path}")
    sys.exit(2)

report = json.loads(benchmark_path.read_text())
thresholds = json.loads(thresholds_path.read_text())

benchmarks = report.get("benchmarks", [])
if not benchmarks:
    print("No benchmark entries found")
    sys.exit(2)

frame_p95 = None
recompose_p95 = None

for benchmark in benchmarks:
    metrics = benchmark.get("metrics", {})
    frame_metric = metrics.get("frameDurationCpuMs")
    if isinstance(frame_metric, dict):
        frame_p95 = max(frame_p95 or 0, float(frame_metric.get("P95", 0)))

    for key, value in metrics.items():
        if "compose:recompose" in key and isinstance(value, dict):
            recompose_p95 = max(recompose_p95 or 0, float(value.get("P95", 0)))

if frame_p95 is None or recompose_p95 is None:
    print("Missing required metrics (frameDurationCpuMs or compose:recompose)")
    sys.exit(2)

print(f"frameDurationCpuMs P95 = {frame_p95}")
print(f"recomposition P95 = {recompose_p95}")

if frame_p95 > thresholds["frameDurationCpuMsP95"]:
    print("Frame-time regression detected")
    sys.exit(1)

if recompose_p95 > thresholds["recompositionCountP95"]:
    print("Recomposition-count regression detected")
    sys.exit(1)

print("Performance gate passed")
