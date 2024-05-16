import os
from collections import defaultdict

def create_map(file_path):
    data_map = defaultdict(set)
    with open(file_path, 'r') as file:
        for line in file.readlines():
            parts = line.split(":")
            if len(parts) == 2:
                source_target_pair = parts[0].split()
                class_ops_pair = parts[1].split("{")[0].strip().split()
                operations = parts[1].split("{")[1].replace("}", "").strip().split(",")
                key = source_target_pair[1] + " " + source_target_pair[2] + " " + class_ops_pair[0]
                values = {operation.strip() for operation in operations}
                data_map[key] = values
    return data_map

def format_line(key, strings):
    keys = key.split(" ")
    key_line = keys[0] + " " + keys[1] + ":" + keys[2] + " "
    comma_separated_values = ", ".join(strings)
    return "Allow " + key_line + " {" + comma_separated_values + "}"

def update_primary_file(xyz_data_map, file_path):
    updated_lines = []
    with open(file_path, 'r') as file:
        for line in file.readlines():
            parts = line.split(":")
            if len(parts) == 2:
                source_target_pair = parts[0].split()
                key = source_target_pair[1] + " " + source_target_pair[2] + " " + parts[1].split("{")[0].strip().split()[0]
                if key in xyz_data_map:
                    new_line = format_line(key, xyz_data_map[key])
                    updated_lines.append(new_line)
                    del xyz_data_map[key]
                else:
                    updated_lines.append(line.strip())

    with open(file_path, 'w') as file:
        for line in updated_lines:
            file.write(line + "\n")

    append_to_file(file_path, xyz_data_map)

def append_to_file(file_path, xyz_data_map):
    with open(file_path, 'a') as file:
        for key, value in xyz_data_map.items():
            file.write(format_line(key, value) + "\n")

def main():
    file_path = "/Users/geetanjali.khabale/practice/src/primary.txt"
    xyz_data_map = create_map(file_path)

    abc_file_path = "/Users/geetanjali.khabale/practice/src/secondary.txt"
    abc_data_map = create_map(abc_file_path)

    for key, value in abc_data_map.items():
        if key in xyz_data_map:
            xyz_data_map[key].update(value)
        else:
            xyz_data_map[key] = value

    update_primary_file(xyz_data_map, file_path)

if __name__ == "__main__":
    main()