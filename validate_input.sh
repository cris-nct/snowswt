# validate_input.sh
if [[ ! "$1" =~ ^[0-9_-]+$ ]]; then
    echo "Invalid input: $1"
    exit 1
fi
