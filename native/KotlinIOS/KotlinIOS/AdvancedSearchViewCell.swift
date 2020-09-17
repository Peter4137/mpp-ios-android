import UIKit

class AdvancedSearchViewCell: UICollectionViewCell {
    
    var delegate: AdvancedSearchCollectionDelegate?
    var dataType: String = ""
    
    @IBOutlet weak var choiceLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.contentView.layer.cornerRadius = 16.0
    }
    
    @IBAction func cancelButton(_ sender: Any) {
        delegate?.removeAdvancedSearchChoice(cell: self)
    }
    
}

protocol AdvancedSearchCollectionDelegate {
    func removeAdvancedSearchChoice(cell: AdvancedSearchViewCell)
}
