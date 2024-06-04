#[derive(Debug, PartialEq)]
pub enum Node<'a> {
    Bracket(Box<Node<'a>>),
    Plus(Box<Node<'a>>, Box<Node<'a>>),
    Minus(Box<Node<'a>>, Box<Node<'a>>),
    Multiply(Box<Node<'a>>, Box<Node<'a>>),
    Divide(Box<Node<'a>>, Box<Node<'a>>),
    Power(Box<Node<'a>>, Box<Node<'a>>),
    Constant(i64),
    Variable(&'a str),
    Function(&'a str, Box<Node<'a>>),
}
