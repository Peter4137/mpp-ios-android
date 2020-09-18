import Foundation

struct AdvancedSearchCellInformation {
    var label: String
    var dataType: cellDataType
}

enum cellDataType {
    case adults
    case children
    case date
}
